package crazypants.enderio.base.conduit.redstone.signals;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class CombinedSignal {

    @NotNull
    public static final CombinedSignal NONE = new CombinedSignal(0);
    @NotNull
    public static final CombinedSignal MAX = new CombinedSignal(15);

    private int strength;

    public CombinedSignal(int strength) {
        this.strength = strength;
    }

    protected void setStrength(int str) {
        if (str > 0) {
            strength = Math.min(str, 15);
        } else {
            strength = 0;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + strength;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CombinedSignal other = (CombinedSignal) obj;
        return strength == other.strength;
    }

    @Override
    public String toString() {
        return "CombinedSignal [strength=" + strength + "]";
    }

}
