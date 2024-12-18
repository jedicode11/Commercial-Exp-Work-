package de.dimedis.mobileentry.util;

public interface DebugUtilsInterface {

    String getDemoAccBarCode();

    String getDemoInitializeBarcode();

    boolean isDemoMode();

    boolean isDemoModeOn();

    String getDefaultToken();

    boolean isAdminMode();

    interface Reinit {
        void reinit();

        void next(int i);
    }

    void onTitleClick(Reinit reinit);
}
