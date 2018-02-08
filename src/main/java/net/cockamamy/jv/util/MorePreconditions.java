package net.cockamamy.jv.util;

import static com.google.common.base.Preconditions.checkArgument;

public final class MorePreconditions {

    private MorePreconditions() { }

    public static void checkArgumentNotNull(final Object argument) {
        checkArgument(argument != null);
    }

    public static void checkArgumentNotBlank(final String argument) {
        checkArgument(argument != null && argument.isEmpty() == false);
    }

}
