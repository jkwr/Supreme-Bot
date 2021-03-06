package de.codemakers.bot.supreme.commands.invoking;

import de.codemakers.bot.supreme.commands.Command;
import java.util.ArrayList;

/**
 * Invoke
 *
 * @author Panzer1119
 */
public class Invoker implements Invokeable {

    public static final ArrayList<Invoker> INVOKERS = createInvokerArrayList();

    private final String invoker;
    private Invokeable invokeable;

    public Invoker(String invoker) {
        this(invoker, null);
    }

    public Invoker(String invoker, Invokeable invokeable) {
        this.invoker = invoker;
        this.invokeable = invokeable;
    }

    public final String getInvoker() {
        return invoker;
    }

    @Override
    public final Invokeable getInvokeable() {
        if (invokeable == null) {
            return null;
        }
        return invokeable.getInvokeable();
    }

    public final Invokeable getDirectInvokeable() {
        return invokeable;
    }

    public final Invoker setInvokeable(Invokeable invokeable) {
        this.invokeable = invokeable;
        return this;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (this == object) {
            return true;
        }
        if (object instanceof Invoker) {
            final Invoker temp = (Invoker) object;
            return getInvoker().equals(temp.getInvoker()) || getInvoker().equalsIgnoreCase(temp.getInvoker());
        } else if (object instanceof String) {
            final String temp = (String) object;
            return getInvoker().equals(temp) || getInvoker().equalsIgnoreCase(temp);
        } else {
            return false;
        }
    }

    @Override
    public final String toString() {
        return getInvoker();
    }

    public static final Invoker createInvoker(String invoker_string) {
        final Invoker invoker = new Invoker(invoker_string);
        INVOKERS.add(invoker);
        return invoker;
    }

    public static final Invoker createInvoker(String invoker_string, Invokeable invokeable) {
        final Invoker invoker = new Invoker(invoker_string, invokeable);
        INVOKERS.add(invoker);
        return invoker;
    }

    public static final boolean deleteInvoker(Invoker invoker) {
        if (invoker == null || !INVOKERS.contains(invoker)) {
            return false;
        }
        INVOKERS.remove(invoker);
        if (invoker.getInvokeable() != null) {
            ((Command) invoker.getInvokeable()).removeInvokers(invoker);
        }
        return true;
    }

    public static final Invoker getInvokerByInvokerString(String invoker_string) {
        if (invoker_string == null || INVOKERS.isEmpty()) {
            return null;
        }
        for (Invoker invoker : INVOKERS) {
            if (invoker.equals(invoker_string)) {
                return invoker;
            }
        }
        return null;
    }

    public static final ArrayList<Invoker> getInvokersByCommand(Command command) {
        if (command == null || INVOKERS.isEmpty()) {
            return new ArrayList<>();
        }
        final ArrayList<Invoker> invokers = new ArrayList<>();
        INVOKERS.stream().filter((invoker) -> command.equals(invoker.getInvokeable())).forEach((invoker) -> {
            invokers.add(invoker);
        });
        return invokers;
    }

    public static final ArrayList<Invoker> createInvokerArrayList() {
        return new ArrayList<Invoker>() {
            @Override
            public boolean add(Invoker invoker) {
                if (invoker == null || contains(invoker)) {
                    return false;
                }
                return super.add(invoker);
            }

            @Override
            public int indexOf(Object object) {
                if (object == null) {
                    return -1;
                } else if (object instanceof Invoker) {
                    for (int i = 0; i < size(); i++) {
                        if (get(i).equals(object)) {
                            return i;
                        }
                    }
                    return -1;
                }
                return -1;
            }
        };
    }

}
