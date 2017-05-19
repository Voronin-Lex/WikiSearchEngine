package Search.Utils;

import java.util.*;

/**
 * Created by lex on 4/6/17.
 */
public class PositionIntersector {

    public  boolean intersectPositionsRecursive(ArrayList<ArrayList<Integer>> positions, int currentRow, int currentcolumn, int windowSize, int currentWindowSize, int prevPosition) {
        int currentPosition = positions.get(currentRow).get(currentcolumn);
        if (currentRow != 0) {
            if (currentPosition > prevPosition)
                currentWindowSize += (currentPosition - prevPosition);
            else
                return false;

            if (currentWindowSize > windowSize)
                return false;
        }
        if (currentRow < positions.size() - 1) {
            for (int i = 0; i < positions.get(currentRow + 1).size(); i++) {
                boolean intersectResult = intersectPositionsRecursive(positions, currentRow + 1, i, windowSize, currentWindowSize, currentPosition);
                if (intersectResult)
                    return true;
            }
            return false;
        } else {
            if (currentWindowSize <= windowSize)
                return true;
            return false;
        }
    }


    public boolean intersectPositions(ArrayList<ArrayList<Integer>> positions, int windowSize) {
        int currentWindowSize = 0;
        for (int i = 0; i < positions.get(0).size(); i++) {
            if (intersectPositionsRecursive(positions, 0, i, windowSize, currentWindowSize, 0))
                return true;
        }
        return false;
    }
}
