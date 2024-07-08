package org.example.interfaces;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.win32.W32APIOptions;

public interface ExtUser32 extends User32 {
    ExtUser32 INSTANCE = (ExtUser32) Native.load("user32", ExtUser32.class, W32APIOptions.DEFAULT_OPTIONS);
    public final static DWORD ENUM_CURRENT_SETTINGS = new DWORD(-1);
    public final static DWORD ENUM_REGISTRY_SETTINGS = new DWORD(-2);
    public final static DWORD MAXIMUM_ALLOWED = new DWORD(0x02000000);

    public final static int CDS_NONE = 0;
    public final static int CDS_UPDATEREGISTRY = 0x00000001;
    public final static int CDS_TEST = 0x00000002;
    public final static int CDS_FULLSCREEN = 0x00000004;
    public final static int CDS_GLOBAL = 0x00000008;
    public final static int CDS_SET_PRIMARY = 0x00000010;
    public final static int CDS_VIDEOPARAMETERS = 0x00000020;
    public final static int CDS_ENABLE_UNSAFE_MODES = 0x00000100;
    public final static int CDS_DISABLE_UNSAFE_MODES = 0x00000200;
    public final static int CDS_RESET = 0x40000000;
    public final static int CDS_RESET_EX = 0x20000000;
    public final static int CDS_NORESET = 0x10000000;

    boolean EnumDisplaySettingsA(String lpszDeviceName, DWORD iModeNum, ExWinGDI.DEVMODEA lpDevMode);
    long ChangeDisplaySettingsExA(byte[] lpszDeviceName, ExWinGDI.DEVMODEA lpDevMode, HWND hwnd, DWORD dwflags, LPVOID lParam);

    @FieldOrder({ "lParam", "wParam", "message" })
    public static class CWPSTRUCT extends Structure {
        public LPARAM lParam;
        public WPARAM wParam;
        public int message;
        public HWND hwnd;
    }

    public final static int HC_ACTION = 0;
    public final static int HC_NOREMOVE = 3;
    public final static int WH_CALLWNDPROC = 4;
    public final static int WH_GETMESSAGE = 3;
    public final static int WH_CALLWNDPROCRET = 12;
    public final static int WH_CBT = 5;
    public final static int WH_DEBUG = 9;
    public final static int WH_SYSMSGFILTER = 6;
    public final static int WM_CREATE = 0x0001;
    public final static int WM_DESTROY = 0x0002;
    public final static int WM_WINDOWPOSCHANGING = 0x0046;

    interface EnumWindowsProc {
        boolean callback(HWND hwnd, CWPSTRUCT lParam);
    }

    interface CallWndProc extends HOOKPROC {
        LRESULT callback(int nCode, WPARAM wParam, CWPSTRUCT lParam);
    }

    interface GetMsgProc extends HOOKPROC {
        LRESULT callback(int nCode, WPARAM wParam, CWPSTRUCT lParam);
    }

    interface SysMsgProc extends HOOKPROC {
        LRESULT callback(int nCode, WPARAM wParam, CWPSTRUCT lParam);
    }

    class HDESK extends PointerType {
        public HDESK() {
        }

        public HDESK(Pointer pointer) {
            super(pointer);
        }
    }
}