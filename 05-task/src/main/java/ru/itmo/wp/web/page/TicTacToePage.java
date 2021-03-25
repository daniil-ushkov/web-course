package ru.itmo.wp.web.page;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class TicTacToePage {
    private State getState(HttpServletRequest request) {
        return (State) request.getSession().getAttribute("state");
    }

    public void action(HttpServletRequest request, Map<String, Object> view) {
        if (getState(request) == null) {
            request.getSession().setAttribute("state", new State(3, 3));
        }
        view.put("state", getState(request));
    }

    public void onMove(HttpServletRequest request, Map<String, Object> view) {
        State currentState = getState(request);
        if (currentState == null) {
            action(request, view);
        } else {
            view.put("state", currentState);
            String key = request.getParameterMap().keySet().stream()
                    .filter(k -> k.matches("cell_\\d\\d"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("request parameters doesn't contain cell"));
            int row = key.charAt(key.length() - 2) - '0';
            int column = key.charAt(key.length() - 1) - '0';
            currentState.move(row, column);
        }
    }

    public void newGame(HttpServletRequest request, Map<String, Object> view) {
        State currentState = getState(request);
        if (currentState == null) {
            action(request, view);
        } else {
            currentState.clear();
            view.put("state", currentState);
        }
    }

    public static class State {
        private final int size;
        private final int goal;
        private Phase phase;
        private int moveNumber;
        private boolean crossesMove;
        private final Symbol[][] cells;

        public State(int size, int goal) {
            this.size = size;
            this.goal = goal;
            this.cells = new Symbol[size][size];
            clear();
        }

        private void clear() {
            phase = Phase.RUNNING;
            moveNumber = 0;
            crossesMove = true;
            for (int i = 0; i < size; ++i) {
                for (int j = 0; j < size; ++j) {
                    cells[i][j] = null;
                }
            }
        }

        private void move(int row, int column) {
            if (phase != Phase.RUNNING || cells[row][column] != null) {
                return;
            }
            cells[row][column] = getSymbol();
            if (checkVictory(row, column)) {
                phase = (getSymbol() == Symbol.X ? Phase.WON_X : Phase.WON_O);
            } else if (moveNumber + 1 == size * size) {
                phase = Phase.DRAW;
            }
            crossesMove = !crossesMove;
            moveNumber++;
        }

        private boolean checkVictory(int row, int column) {
            for (int deltaRow = -1; deltaRow <= 1; ++deltaRow) {
                for (int deltaCol = -1; deltaCol <= 1; ++deltaCol) {
                    if ((deltaRow != 0 || deltaCol != 0) && countDir(row, column, deltaRow, deltaCol)
                            + countDir(row, column, -deltaRow, -deltaCol) >= goal + 1) {
                        return true;
                    }
                }
            }
            return false;
        }

        private int countDir(int row, int column, int deltaRow, int deltaCol) {
            int counter = 0;
            while (validCell(row, column) &&
                    cells[row][column] == getSymbol()) {
                counter++;
                row += deltaRow;
                column += deltaCol;
            }
            return counter;
        }

        private boolean validCell(int row, int column) {
            return 0 <= row && row < size && 0 <= column && column < size;
        }

        private Symbol getSymbol() {
            return crossesMove ? Symbol.X : Symbol.O;
        }

        public int getSize() {
            return size;
        }

        public Phase getPhase() {
            return phase;
        }

        public boolean isCrossesMove() {
            return crossesMove;
        }

        public Symbol[][] getCells() {
            return cells;
        }
    }

    public enum Phase {
        RUNNING, WON_X, WON_O, DRAW
    }

    public enum Symbol {
        X, O
    }
}
