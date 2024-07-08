package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.PointerByReference;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.SneakyThrows;
import mmarquee.automation.AutomationException;
import mmarquee.automation.Element;
import mmarquee.automation.UIAutomation;
import mmarquee.uiautomation.TreeScope;
import org.example.dto.FindUrls;
import org.example.dto.WindowSize;
import org.example.interfaces.ExWinGDI;
import org.example.interfaces.ExtUser32;
import org.example.interfaces.ServerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@EnableAsync
public class ServerServiceImpl implements ServerService {

    @Value("${chrome.path}")
    private String chromePath;

    private final Set<FindUrls> urlsSet = new HashSet<>();

    private UIAutomation automation = UIAutomation.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private ExWinGDI.DEVMODEA defaultScreenSize;
    private final User32.INPUT input = new User32.INPUT();
    private ExecutorService findUrlExecutor = Executors.newFixedThreadPool(10);

    @PostConstruct
    private void getDefaultScreenSize() throws Exception {
        defaultScreenSize = ExWinGDI.DEVMODEA.newInstance(ExWinGDI.DEVMODEA.class);
        if (!ExtUser32.INSTANCE.EnumDisplaySettingsA(null, ExtUser32.ENUM_CURRENT_SETTINGS, defaultScreenSize)) {
            throw new Exception("Can`t get default screen size");
        }
        input.type = new WinDef.DWORD(User32.INPUT.INPUT_MOUSE);
        input.input.setType("mi");
        input.input.mi.dwFlags = new WinDef.DWORD(0x0800);
    }

    @PreDestroy
    private void preDestroy() {
        findUrlExecutor.shutdown();
    }

    public String openChrome() {
        String url = "https://www.google.com/search?q=слоны";
        String response = "";
        try {
            Process p = Runtime.getRuntime().exec(new String[] {chromePath, url});
            p.waitFor();
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getChromeSize();
    }

    public String getChromeSize() {
        String response = "";
        try {
            if (automation.getFocusedElement() != null) {
                WinDef.RECT rect = automation.getFocusedElement().getBoundingRectangle();
                WindowSize windowSize = new WindowSize(rect.top, rect.bottom, rect.left, rect.right);
                response = objectMapper.writeValueAsString(windowSize);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    @Async
    public void mouseMove(int x, int y) {
        try {
            User32.INSTANCE.SetCursorPos(x, y);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    @Async
    @SneakyThrows
    public void mouseScroll(int scroll) {
        try {
            input.input.mi.mouseData = new WinDef.DWORD(scroll * 120);
            User32.INPUT[] inputs = {input};
            User32.INSTANCE.SendInput(new WinDef.DWORD(inputs.length), inputs, input.size());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void searchUrl() {
        try {
            if (automation.getFocusedElement() != null) {
                Element element = automation.getFocusedElement();
                if (element.getName().contains("слоны")) {
                    try {
                        PointerByReference pbr = automation.createTrueCondition();
                        List<Element> elements = element.findAll(new TreeScope(4), pbr);
                        findAllVisibleUrls(elements);
                    } catch (AutomationException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (AutomationException e) {
            e.printStackTrace();
        }
    }

    private void findAllVisibleUrls(List<Element> elements) {
        for (Element e : elements) {
            findUrlExecutor.execute(() -> {
                try {
                    if (!e.offScreen()) {
                        equalsRegex(e.getName());
                    }
                } catch (AutomationException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    private void equalsRegex(String s) {
        Pattern pattern = Pattern.compile("https://\\S+");
        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            String url = matcher.group();
            urlsSet.add(new FindUrls(url, getScreenSize(), LocalDateTime.now()));
        }
    }

    private String getScreenSize() {
        int width = User32.INSTANCE.GetSystemMetrics(User32.SM_CXSCREEN);
        int height = User32.INSTANCE.GetSystemMetrics(User32.SM_CYSCREEN);
        return String.format("%sx%s", width, height);
    }

    public FindUrls searchNotSendUrls() {
        for(FindUrls url : urlsSet) {
            if(!url.isSend()) {
                url.setSend(true);
                return url;
            }
        }
        return null;
    }

    @Override
    public void resize() {
        Random random = new Random();
        List<ExWinGDI.DEVMODEA> modes = getDisplayModes();
        ExWinGDI.DEVMODEA mode;
        do {
            mode = modes.get(random.nextInt(modes.size()));
        } while (!setScreenSize(mode));
    }

    private List<ExWinGDI.DEVMODEA> getDisplayModes() {
        Set<ExWinGDI.DEVMODEA> modes = new TreeSet<>(Comparator.comparing((ExWinGDI.DEVMODEA o) -> o.dmPelsWidth)
                .thenComparing(o -> o.dmPelsHeight));
        WinDef.DWORD counter = new WinDef.DWORD(0);
        while(true) {
            ExWinGDI.DEVMODEA mode = ExWinGDI.DEVMODEA.newInstance(ExWinGDI.DEVMODEA.class);
            if (ExtUser32.INSTANCE.EnumDisplaySettingsA(null, counter, mode)){
                counter.setValue(counter.longValue() + 1);
                modes.add(mode);
            } else break;
        }
        return modes.stream().toList();
    }

    private boolean setScreenSize(ExWinGDI.DEVMODEA mode) {
        WinDef.DWORD dwFlags = new WinDef.DWORD(ExtUser32.CDS_UPDATEREGISTRY | ExtUser32.CDS_GLOBAL | ExtUser32.CDS_RESET);
        if (mode.dmPelsWidth.compareTo(defaultScreenSize.dmPelsWidth) == 0 && mode.dmPelsHeight.compareTo(defaultScreenSize.dmPelsHeight) == 0) return false;
        ExtUser32.INSTANCE.ChangeDisplaySettingsExA(null, mode, null, dwFlags, null);
        return true;
    }

}