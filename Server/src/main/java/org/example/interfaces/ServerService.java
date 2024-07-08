package org.example.interfaces;

import org.example.dto.FindUrls;

public interface ServerService {
    String openChrome();
    String getChromeSize();
    void mouseMove(int x, int y);
    void mouseScroll(int scroll);
    void searchUrl();
    void resize();
    FindUrls searchNotSendUrls();
}
