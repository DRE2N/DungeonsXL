/*
 * Copyright (C) 2014-2023 Daniel Saukel
 *
 * This library is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNULesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.erethon.dungeonsxl.api.trigger;

import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.world.GameWorld;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a logical expression of elements in an AND and OR relation to each other.
 * <p>
 * Valid operators are:
 * <ul>
 * <li>, = AND</li>
 * <li>/ = OR</li>
 * <li>() = priority</li>
 * </ul>
 * OR is prioritized over AND if no brackets indicate otherwise.
 * <p>
 * Logical expressions tolerate spaces and redundant operators, but no wrong brackets.
 *
 * @author Daniel Saukel
 */
public class LogicalExpression {

    private enum ComponentType {
        FIRST,
        AND,
        OR;

        @Override
        public String toString() {
            return this == FIRST ? "" : name() + ":";
        }
    }

    /**
     * A satisfied, empty expression.
     */
    public static final LogicalExpression EMPTY = new LogicalExpression(ComponentType.FIRST, "");

    static {
        EMPTY.satisfied = true;
    }

    private ComponentType type;
    private String text;
    private Trigger trigger;
    private List<LogicalExpression> contents;
    private boolean satisfied;

    private LogicalExpression(ComponentType type, String text) {
        this.type = type;
        this.text = text;
        contents = new ArrayList<>();
    }

    /**
     * Interpretes the given string as a expression.
     *
     * @param string the string to parse
     * @throws IllegalArgumentException if the string is not a valid logical expression.
     * @return a expression that represents the given string; null if erroneous
     */
    public static LogicalExpression parse(String string) {
        if (string == null || string.isBlank()) {
            return null;
        }
        string = string.replace(" ", "");

        // Crop redundant end
        int i = string.length() - 1;
        while (string.charAt(i) == ',' || string.charAt(i) == '/') {
            i--;
        }

        string = string.substring(0, i + 1);
        return parse(ComponentType.FIRST, string);
    }

    private static LogicalExpression parse(ComponentType type, String string) {
        LogicalExpression root = new LogicalExpression(type, string);
        if (!string.contains(",") && !string.contains("/")) {
            return root;
        }

        int i = 0;
        int bracketStart = -1;
        int innerBrackets = 0;
        int currentCompStart = 0;
        boolean bracketLegal = true;
        ComponentType currentCompType = ComponentType.FIRST;
        while (string.length() > i) {
            char c = string.charAt(i);
            if (c == '(') {
                if (!bracketLegal) {
                    throw new IllegalArgumentException("Bracket on illegal position at index " + i);
                }
                if (bracketStart == -1) {
                    bracketStart = i + 1; // One after bracket start
                } else {
                    innerBrackets++;
                }

            } else if (c == ')') {
                if (bracketStart == -1) {
                    throw new IllegalArgumentException("Closed bracket before it was opened at index " + i);
                }
                innerBrackets--;
                if (innerBrackets == -1) {
                    // Bracket closed
                    LogicalExpression subexpression = parse(currentCompType, string.substring(bracketStart, i));
                    if (bracketStart == 1 && i == string.length() - 1) {
                        // Bracket encapsulates the whole string and was redundant
                        return subexpression;
                    }

                    root.contents.add(subexpression);
                    innerBrackets = 0;
                    bracketStart = -1;
                    currentCompStart = i + 2;
                }
                bracketLegal = false;

            } else if (bracketStart == -1 && (c == ',' || c == '/')) {
                if (currentCompStart == i) {
                    // Multiple commas / leading comma -> ignore
                    currentCompStart++;
                } else {
                    // Component had ended
                    if (i > currentCompStart) {
                        // Component had ended
                        root.contents.add(parse(currentCompType, string.substring(currentCompStart, i)));
                    }
                    currentCompStart = i + 1;
                    currentCompType = c == ',' ? ComponentType.AND : ComponentType.OR;
                }
                bracketLegal = true;
            } else {
                bracketLegal = false;
            }
            i++;
        }
        if (innerBrackets != 0 || bracketStart != -1) {
            throw new IllegalArgumentException("Bracket never closed");
        }
        // Last component
        if (currentCompStart < string.length()) {
            LogicalExpression comp = parse(currentCompType, string.substring(currentCompStart, i));
            if (comp != null) {
                root.contents.add(comp);
            }
        }
        return root;
    }

    /**
     * Returns the text this expression wraps.
     * <p>
     * This is not guaranteed to be equal to the string that had been passed to {@link #parse(java.lang.String)} as it may be stripped from redundant symbols.
     *
     * @return the text this expression wraps
     */
    public String getText() {
        return text;
    }

    /**
     * Returns if this expression only consists of exactly one component.
     *
     * @return if this expression only consists of exactly one component
     */
    public boolean isAtomic() {
        return contents.isEmpty();
    }

    void setTrigger(Trigger trigger) {
        this.trigger = trigger;
    }

    /**
     * Creates a {@link Trigger} from an atomic element of the expression.
     *
     * @param api      the {@link DungeonsAPI} reference; not null
     * @param listener the listener that belongs to the trigger; not null
     * @param generic  if a generic trigger should be created if there is no specific trigger identifier (e.g. second line of a trigger sign)
     * @throws IllegalArgumentException if the listener is null or not in a {@link GameWorld}
     * @return the {@link Trigger} the string represents; null if there is none.
     */
    public Trigger toTrigger(DungeonsAPI api, TriggerListener listener, boolean generic) {
        if (trigger != null) {
            return trigger;
        }
        if (!isAtomic()) {
            return null;
        }
        if (listener == null || listener.getGameWorld() == null) {
            throw new IllegalArgumentException("Listener must not be null and must be in a game world");
        }
        listener.getGameWorld().createTrigger(listener, this);
        return trigger;
    }

    /**
     * Returns a List of the contents of this expression.<p>
     * Changes made to this list do not update the expression.
     *
     * @param deep if true, brackets are resolved to atomic components; if false, brackets are one element in the list
     * @return a List of the contents the contents of this expression
     */
    public List<LogicalExpression> getContents(boolean deep) {
        if (!deep || isAtomic()) {
            return new ArrayList<>(contents);
        }
        List<LogicalExpression> atomicContents = new ArrayList<>();
        for (LogicalExpression comp : contents) {
            if (comp.isAtomic()) {
                atomicContents.add(comp);
            } else {
                atomicContents.addAll(comp.getContents(true));
            }
        }
        return atomicContents;
    }

    /**
     * Returns if this expression is satisfied.
     *
     * @return if this expression is satisfied
     */
    public boolean isSatisfied() {
        if (isAtomic()) {
            return satisfied;
        }

        int[] orChains = new int[contents.size()];
        int ors = 0;
        boolean inChain = false;
        boolean chainSatisfied = false;
        // Check OR chains, return false if NONE is satisfied
        for (int i = 1; contents.size() > i; i++) {
            if (contents.get(i).type != ComponentType.OR) {
                if (inChain) {
                    if (!chainSatisfied) {
                        return false;
                    }
                    orChains[ors] = i - 1; // write to even index
                    inChain = false;
                    chainSatisfied = false;
                    ors++; // switch to even index for possible upcoming chain
                }
                continue;
            }
            if (!inChain) {
                inChain = true;
                orChains[ors] = i - 1; // write to even index
                chainSatisfied = contents.get(i - 1).isSatisfied();
                ors++;                 // switch to uneven index
            } else {
                orChains[ors] = i;     // update uneven index
            }
            if (contents.get(i).isSatisfied()) {
                chainSatisfied = true;
            }
        }
        if (inChain) {
            if (!chainSatisfied) {
                return false;
            }
            orChains[ors] = contents.size() - 1;
        }

        // Check AND chains, return false if ONE IS NOT satisfied
        int i = 0, j = 0;
        while (contents.size() > i) {
            if (!(orChains[j] == 0 && orChains[j + 1] == 0)) {
                while (i < orChains[j]) {
                    if (!contents.get(i).isSatisfied()) {
                        return false;
                    }
                    i++;
                }
                j++;
                i = orChains[j] + 1;
                j++;

            } else {
                if (!contents.get(i).isSatisfied()) {
                    return false;
                }
                i++;
            }
        }
        return true;
    }

    /**
     * Sets the value of an atomic expression to the given boolean.
     * <p>
     * If the expression is not atomic, update the atomic components instead.
     *
     * @param satisfied if the expression is satisfied
     * @return if the expression could be updated (false if the expression is not atomic)
     */
    public boolean setSatisfied(boolean satisfied) {
        if (!isAtomic()) {
            return false;
        }
        this.satisfied = satisfied;
        return true;
    }

    @Override
    public String toString() {
        if (contents.isEmpty()) {
            return type + text + (isSatisfied() ? "(t)" : "(f)");
        }
        return type + contents.toString() + (isSatisfied() ? "(t)" : "(f)");
    }

}
