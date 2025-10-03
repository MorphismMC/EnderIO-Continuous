package crazypants.enderio.base.machine.interfaces;

import java.util.Set;

import crazypants.enderio.api.Localizable;
import org.jetbrains.annotations.NotNull;

public interface Notifiable {

    @NotNull
    Set<? extends Localizable> getNotification();

}
