package org.example.interfaces;

import com.sun.jna.NativeLong;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinGDI;

public interface ExWinGDI extends WinGDI {
    public final static int DM_BITSPERPEL = 0x00040000;
    public final static int DM_PELSWIDTH = 0x00080000;
    public final static int DM_POSITION = 0x00000020;
    public final static int DM_PELSHEIGHT = 0x00100000;
    public static final int DISPLAY_DEVICE_PRIMARY_DEVICE = 4;

    @Structure.FieldOrder({ "cb", "DeviceName", "DeviceString", "stateFlags", "DeviceID", "DeviceKey" })
    public static class DISPLAY_DEVICEA extends Structure {
        public WinDef.DWORD cb;
        public byte[] DeviceName = new byte[32];
        public byte[] DeviceString = new byte[128];
        public WinDef.DWORD stateFlags;
        public byte[] DeviceID = new byte[128];
        public byte[] DeviceKey = new byte[128];
    }

    @Structure.FieldOrder({ "x", "y" })
    public static class POINTL extends Structure {
        public NativeLong x;
        public NativeLong y;
    }

    @Structure.FieldOrder({ "dmDeviceName", "dmSpecVersion", "dmDriverVersion", "dmSize", "dmDriverExtra", "dmFields", "dmUnion1", "dmColor",
            "dmDuplex", "dmYResolution", "dmTTOption", "dmCollate", "dmFormName", "dmLogPixels", "dmBitsPerPel", "dmPelsWidth",
            "dmPelsHeight", "dummyunionname2", "dmDisplayFrequency", "dmICMMethod", "dmICMIntent", "dmMediaType", "dmDitherType",
            "dmReserved1", "dmReserved2", "dmPanningWidth", "dmPanningHeight" })
    public static class DEVMODEA extends Structure {
        private final static int CCHDEVICENAME = 32;
        private final static int CCHFORMNAME = 32;
        public byte[] dmDeviceName = new byte[CCHDEVICENAME];
        public WinDef.WORD dmSpecVersion;
        public WinDef.WORD dmDriverVersion;
        public WinDef.WORD dmSize;
        public WinDef.WORD dmDriverExtra;
        public WinDef.DWORD dmFields;
        public DUMMYUNIONNAME dmUnion1;

        public static class DUMMYUNIONNAME extends Union {
            public DUMMYSTRUCTNAME dummystructname;

            @FieldOrder({ "dmOrientation", "dmPaperSize", "dmPaperLength", "dmPaperWidth", "dmScale", "dmCopies", "dmDefaultSource",
                    "dmPrintQuality" })
            public static class DUMMYSTRUCTNAME extends Structure {
                public short dmOrientation;
                public short dmPaperSize;
                public short dmPaperLength;
                public short dmPaperWidth;
                public short dmScale;
                public short dmCopies;
                public short dmDefaultSource;
                public short dmPrintQuality;

                public DUMMYSTRUCTNAME() {
                    super();
                }
            }

            public POINTL dmPosition;
            public DUMMYSTRUCTNAME2 dummystructname2;

            @FieldOrder({ "dmPosition", "dmDisplayOrientation", "dmDisplayFixedOutput" })
            public static class DUMMYSTRUCTNAME2 extends Structure {
                public POINTL dmPosition;
                public WinDef.DWORD dmDisplayOrientation;
                public WinDef.DWORD dmDisplayFixedOutput;

                public DUMMYSTRUCTNAME2() {
                    super();
                }
            }
        }

        public short dmColor;
        public short dmDuplex;
        public short dmYResolution;
        public short dmTTOption;
        public short dmCollate;
        public byte[] dmFormName = new byte[CCHFORMNAME];
        public WinDef.WORD dmLogPixels;
        public WinDef.DWORD dmBitsPerPel;
        public WinDef.DWORD dmPelsWidth;
        public WinDef.DWORD dmPelsHeight;
        public DUMMYUNIONNAME2 dummyunionname2;

        public static class DUMMYUNIONNAME2 extends Union {
            public WinDef.DWORD dmDisplayFlags;
            public WinDef.DWORD dmNup;
        }

        public WinDef.DWORD dmDisplayFrequency;
        public WinDef.DWORD dmICMMethod;
        public WinDef.DWORD dmICMIntent;
        public WinDef.DWORD dmMediaType;
        public WinDef.DWORD dmDitherType;
        public WinDef.DWORD dmReserved1;
        public WinDef.DWORD dmReserved2;
        public WinDef.DWORD dmPanningWidth;
        public WinDef.DWORD dmPanningHeight;
    }
}