package crazypants.enderio.base.filter;

import org.jetbrains.annotations.NotNull;

public interface FilterContainer<I extends Filter> {

    @NotNull
    I getFilter(int filterIndex);

}
