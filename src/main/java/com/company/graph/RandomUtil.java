package com.company.graph;

import java.util.PriorityQueue;
import java.util.Random;

public class RandomUtil {
    private final Random m_random;
    java.util.PriorityQueue<Integer> m_excludeList;

    int m_start=0;
    int m_end=0;

    /**
     * @param start start of range (inclusive)
     * @param end end of range (exclusive)
     */
    public RandomUtil(int start, int end) {
        m_random = new Random(System.currentTimeMillis());
        m_start=start;
        m_end=end;
        m_excludeList = new PriorityQueue<Integer>();
    }

    /**
     * @return the random number within start-end but not one of excludes. Otherwise (no free random) returns -1
     */
    public int getNonRepeatingRandomInt() {
        Integer[] array = m_excludeList.toArray (new Integer [m_excludeList.size()]);
        int toExclude= getRandomWithExclusion(m_random,m_start,m_end, array);
        if(!m_excludeList.contains(toExclude)) {
            //it's a new valid random!
            m_excludeList.add(toExclude);
            return toExclude;
        }
        else {
            return -1;
        }
    }

    /**
     * @param start start of range (inclusive)
     * @param end end of range (exclusive)
     * @param exclude numbers to exclude (= numbers you do not want)
     * @return the random number within start-end but not one of excludes.
     */
    public int getRandomWithExclusion(Random rnd, int start, int end, Integer... exclude) {
        int random = start + rnd.nextInt(end - start + 1 - exclude.length);
        for (int ex : exclude) {
            if (random < ex) {
                break;
            }
            random++;
        }
        return random;
    }
}
