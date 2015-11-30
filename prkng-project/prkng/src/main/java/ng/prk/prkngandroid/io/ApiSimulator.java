package ng.prk.prkngandroid.io;

import java.util.HashMap;
import java.util.Map;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.model.RestrInterval;
import ng.prk.prkngandroid.model.RestrIntervalsList;
import ng.prk.prkngandroid.util.CalendarUtils;


public class ApiSimulator {
    private final static String TAG = "ApiSimulator";

    public static Map<Integer, RestrIntervalsList> getTestScenarios() {
        return getTestScenario(Const.UNKNOWN_VALUE);
    }

    public static Map<Integer, RestrIntervalsList> getTestScenario(int scenario) {
        final int today = CalendarUtils.getIsoDayOfWeek();
        final int dayOfWeek = CalendarUtils.getIsoDayOfWeekLooped(1, today);

        final Map<Integer, RestrIntervalsList> daysMap = new HashMap<>();
        for (int i = 1; i <= CalendarUtils.WEEK_IN_DAYS; i++) {
            RestrIntervalsList restrList = daysMap.get(i);
            if (restrList == null) {
                // Initialize the day's array if necessary
                restrList = new RestrIntervalsList();
                daysMap.put(i, restrList);
            }
        }

        if (scenario == Const.UNKNOWN_VALUE) {
            int i = 1;
//            buildScenario01(daysMap, i++);
//            buildScenario02(daysMap, i++);
//
//            // Recursive tests
//            buildScenario03(daysMap, i++);
//            buildScenario03A(daysMap, i++);
//            buildScenario03B(daysMap, i++);
//
//            buildScenario04(daysMap, i++);
//            buildScenario05(daysMap,i++);
//            buildScenario06(daysMap, i++);
//            buildScenario07(daysMap, i++);
//
//            // TimeMax + NoParking
//            buildScenario08(daysMap, i++);
//            buildScenario09(daysMap, i++);

            // No overrules (combined TimeMaxPaid)
            buildScenario10(daysMap, i++);
            buildScenario11(daysMap, i++);
            buildScenario12(daysMap, i++);
            buildScenario12A(daysMap, i++);

        } else
            switch (scenario) {
                case 1:
                    buildScenario01(daysMap, dayOfWeek);
                    break;
                case 2:
                    buildScenario02(daysMap, dayOfWeek);
                    break;
                case 3:
                    buildScenario03(daysMap, dayOfWeek);
                    break;
                case 4:
                    buildScenario04(daysMap, dayOfWeek);
                    break;
                case 5:
                    buildScenario05(daysMap, dayOfWeek);
                    break;
                case 6:
                    buildScenario06(daysMap, dayOfWeek);
                    break;
                case 7:
                    buildScenario07(daysMap, dayOfWeek);
                    break;
            }


        return daysMap;
    }

    /**
     * Paid 9-17
     * NoParking 12-14
     *
     * @param daysMap
     * @param dayOfWeek
     */
    private static void buildScenario01(Map<Integer, RestrIntervalsList> daysMap, int dayOfWeek) {
        RestrIntervalsList restrList = daysMap.get(dayOfWeek);

        restrList.addMerge(
                new RestrInterval(dayOfWeek,
                        9f,
                        17f,
                        Const.ParkingRestrType.PAID,
                        Const.UNKNOWN_VALUE)
        );
        restrList.addMerge(
                new RestrInterval(dayOfWeek,
                        12f,
                        14f,
                        Const.ParkingRestrType.ALL_TIMES,
                        Const.UNKNOWN_VALUE)
        );
    }

    /**
     * NoParking 9-17
     * Paid 12-14
     *
     * @param daysMap
     * @param dayOfWeek
     */
    private static void buildScenario02(Map<Integer, RestrIntervalsList> daysMap, int dayOfWeek) {
        RestrIntervalsList restrList = daysMap.get(dayOfWeek);

        restrList.addMerge(
                new RestrInterval(dayOfWeek,
                        9f,
                        17f,
                        Const.ParkingRestrType.ALL_TIMES,
                        Const.UNKNOWN_VALUE)
        );
        restrList.addMerge(
                new RestrInterval(dayOfWeek,
                        12f,
                        14f,
                        Const.ParkingRestrType.PAID,
                        Const.UNKNOWN_VALUE)
        );
    }

    /**
     * NoParking 5-12
     * Paid 11-15
     * NoParking 14-18
     * Paid 17-19
     *
     * @param daysMap
     * @param dayOfWeek
     */
    private static void buildScenario03(Map<Integer, RestrIntervalsList> daysMap, int dayOfWeek) {
        RestrIntervalsList restrList = daysMap.get(dayOfWeek);

        restrList.addMerge(
                new RestrInterval(dayOfWeek,
                        14f,
                        18f,
                        Const.ParkingRestrType.ALL_TIMES,
                        Const.UNKNOWN_VALUE)
        );
        restrList.addMerge(
                new RestrInterval(dayOfWeek,
                        5f,
                        12f,
                        Const.ParkingRestrType.ALL_TIMES,
                        Const.UNKNOWN_VALUE)
        );
        restrList.addMerge(
                new RestrInterval(dayOfWeek,
                        11f,
                        15f,
                        Const.ParkingRestrType.PAID,
                        Const.UNKNOWN_VALUE)
        );
        restrList.addMerge(
                new RestrInterval(dayOfWeek,
                        17f,
                        19f,
                        Const.ParkingRestrType.PAID,
                        Const.UNKNOWN_VALUE)
        );
    }


    private static void buildScenario03A(Map<Integer, RestrIntervalsList> daysMap, int dayOfWeek) {
        RestrIntervalsList restrList = daysMap.get(dayOfWeek);

        // PAID 3-23
        restrList.addMerge(
                new RestrInterval(dayOfWeek,
                        3f,
                        23f,
                        Const.ParkingRestrType.PAID,
                        Const.UNKNOWN_VALUE)
        );

        buildScenario03(daysMap, dayOfWeek);
    }

    private static void buildScenario03B(Map<Integer, RestrIntervalsList> daysMap, int dayOfWeek) {
        buildScenario03(daysMap, dayOfWeek);

        RestrIntervalsList restrList = daysMap.get(dayOfWeek);

        // NoParking 3-23
        restrList.addMerge(
                new RestrInterval(dayOfWeek,
                        3f,
                        23f,
                        Const.ParkingRestrType.PAID,
                        Const.UNKNOWN_VALUE)
        );
    }

    /**
     * Paid 5-12
     * NoParking 11-15
     * Paid 14-18
     * NoParking 17-19
     *
     * @param daysMap
     * @param dayOfWeek
     */
    private static void buildScenario04(Map<Integer, RestrIntervalsList> daysMap, int dayOfWeek) {
        RestrIntervalsList restrList = daysMap.get(dayOfWeek);

        restrList.addMerge(
                new RestrInterval(dayOfWeek,
                        14f,
                        18f,
                        Const.ParkingRestrType.PAID,
                        Const.UNKNOWN_VALUE)
        );
        restrList.addMerge(
                new RestrInterval(dayOfWeek,
                        5f,
                        12f,
                        Const.ParkingRestrType.PAID,
                        Const.UNKNOWN_VALUE)
        );
        restrList.addMerge(
                new RestrInterval(dayOfWeek,
                        11f,
                        15f,
                        Const.ParkingRestrType.ALL_TIMES,
                        Const.UNKNOWN_VALUE)
        );
        restrList.addMerge(
                new RestrInterval(dayOfWeek,
                        17f,
                        19f,
                        Const.ParkingRestrType.ALL_TIMES,
                        Const.UNKNOWN_VALUE)
        );
    }

    /**
     * NoParking 9-16
     * Paid 15-18
     *
     * @param daysMap
     * @param dayOfWeek
     */
    private static void buildScenario05(Map<Integer, RestrIntervalsList> daysMap, int dayOfWeek) {
        RestrIntervalsList restrList = daysMap.get(dayOfWeek);

        restrList.addMerge(
                new RestrInterval(dayOfWeek,
                        9f,
                        16f,
                        Const.ParkingRestrType.ALL_TIMES,
                        Const.UNKNOWN_VALUE)
        );
        restrList.addMerge(
                new RestrInterval(dayOfWeek,
                        15f,
                        18f,
                        Const.ParkingRestrType.PAID,
                        Const.UNKNOWN_VALUE)
        );
    }

    /**
     * Paid 9-16
     * NoParking 15-18
     *
     * @param daysMap
     * @param dayOfWeek
     */
    private static void buildScenario06(Map<Integer, RestrIntervalsList> daysMap, int dayOfWeek) {
        RestrIntervalsList restrList = daysMap.get(dayOfWeek);

        restrList.addMerge(
                new RestrInterval(dayOfWeek,
                        9f,
                        16f,
                        Const.ParkingRestrType.PAID,
                        Const.UNKNOWN_VALUE)
        );
        restrList.addMerge(
                new RestrInterval(dayOfWeek,
                        15f,
                        18f,
                        Const.ParkingRestrType.ALL_TIMES,
                        Const.UNKNOWN_VALUE)
        );
    }

    /**
     * Paid 9-12
     * NoParking 14-17
     *
     * @param daysMap
     * @param dayOfWeek
     */
    private static void buildScenario07(Map<Integer, RestrIntervalsList> daysMap, int dayOfWeek) {
        RestrIntervalsList restrList = daysMap.get(dayOfWeek);

        restrList.addMerge(
                new RestrInterval(dayOfWeek,
                        9f,
                        12f,
                        Const.ParkingRestrType.PAID,
                        Const.UNKNOWN_VALUE)
        );
        restrList.addMerge(
                new RestrInterval(dayOfWeek,
                        14f,
                        17f,
                        Const.ParkingRestrType.ALL_TIMES,
                        Const.UNKNOWN_VALUE)
        );
    }

    /**
     * TimeMax (60min) 9-12
     * NoParking 12-17
     *
     * @param daysMap
     * @param dayOfWeek
     */
    private static void buildScenario08(Map<Integer, RestrIntervalsList> daysMap, int dayOfWeek) {
        RestrIntervalsList restrList = daysMap.get(dayOfWeek);

        restrList.addMerge(
                new RestrInterval(dayOfWeek,
                        9f,
                        12f,
                        Const.ParkingRestrType.TIME_MAX,
                        60)
        );
        restrList.addMerge(
                new RestrInterval(dayOfWeek,
                        12f,
                        17f,
                        Const.ParkingRestrType.ALL_TIMES,
                        Const.UNKNOWN_VALUE)
        );
    }

    /**
     * NoParking 9-17
     * TimeMax (60min) 9-12
     *
     * @param daysMap
     * @param dayOfWeek
     */
    private static void buildScenario09(Map<Integer, RestrIntervalsList> daysMap, int dayOfWeek) {
        RestrIntervalsList restrList = daysMap.get(dayOfWeek);

        restrList.addMerge(
                new RestrInterval(dayOfWeek,
                        9f,
                        17f,
                        Const.ParkingRestrType.ALL_TIMES,
                        Const.UNKNOWN_VALUE)
        );
        restrList.addMerge(
                new RestrInterval(dayOfWeek,
                        9f,
                        12f,
                        Const.ParkingRestrType.TIME_MAX,
                        60)
        );
    }

    /**
     * Paid 9-14
     * TimeMax (60min) 12-17
     *
     * @param daysMap
     * @param dayOfWeek
     */
    private static void buildScenario10(Map<Integer, RestrIntervalsList> daysMap, int dayOfWeek) {
        RestrIntervalsList restrList = daysMap.get(dayOfWeek);

        restrList.addMerge(
                new RestrInterval(dayOfWeek,
                        9f,
                        14f,
                        Const.ParkingRestrType.PAID,
                        Const.UNKNOWN_VALUE)
        );
        restrList.addMerge(
                new RestrInterval(dayOfWeek,
                        12f,
                        17f,
                        Const.ParkingRestrType.TIME_MAX,
                        60)
        );
    }

    /**
     * TimeMax (30min) 9-14
     * TimeMaxPaid (120min) 12-17
     *
     * @param daysMap
     * @param dayOfWeek
     */
    private static void buildScenario11(Map<Integer, RestrIntervalsList> daysMap, int dayOfWeek) {
        RestrIntervalsList restrList = daysMap.get(dayOfWeek);

        restrList.addMerge(
                new RestrInterval(dayOfWeek,
                        9f,
                        14f,
                        Const.ParkingRestrType.TIME_MAX,
                        30)
        );
        restrList.addMerge(
                new RestrInterval(dayOfWeek,
                        12f,
                        17f,
                        Const.ParkingRestrType.TIME_MAX_PAID,
                        120)
        );
    }

    /**
     * Paid 9-17
     * TimeMaxPaid (60min) 12-14
     *
     * @param daysMap
     * @param dayOfWeek
     */
    private static void buildScenario12(Map<Integer, RestrIntervalsList> daysMap, int dayOfWeek) {
        RestrIntervalsList restrList = daysMap.get(dayOfWeek);

        restrList.addMerge(
                new RestrInterval(dayOfWeek,
                        9f,
                        17f,
                        Const.ParkingRestrType.PAID,
                        Const.UNKNOWN_VALUE)
        );
        restrList.addMerge(
                new RestrInterval(dayOfWeek,
                        12f,
                        14f,
                        Const.ParkingRestrType.TIME_MAX_PAID,
                        60)
        );
    }

    private static void buildScenario12A(Map<Integer, RestrIntervalsList> daysMap, int dayOfWeek) {
        buildScenario12(daysMap, dayOfWeek);

        RestrIntervalsList restrList = daysMap.get(dayOfWeek);

        // TimeMax 30min
        restrList.addMerge(
                new RestrInterval(dayOfWeek,
                        3f,
                        23f,
                        Const.ParkingRestrType.TIME_MAX,
                        30)
        );
    }
}
