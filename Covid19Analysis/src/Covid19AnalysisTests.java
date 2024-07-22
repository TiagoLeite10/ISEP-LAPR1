import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;

public class Covid19AnalysisTests {

    private static final String SEPARATOR = Covid19Analysis.fillString('-', 24);
    private static final String OK_MESSAGE = " --> OK";
    private static final String NOT_OK_MESSAGE = " --> NOT OK";

    public static void main(String[] args) throws ParseException, IOException {
        runTests();
    }

    // Executa todos os testes
    public static void runTests() throws IOException, ParseException {

        int[][] covidAccumulatedDailyData = new int[Covid19Analysis.MAXIMUM_RECORDS][Covid19Analysis.TOTAL_FIELDS];
        int[][] covidTotalDailyData = new int[Covid19Analysis.MAXIMUM_RECORDS][Covid19Analysis.TOTAL_FIELDS];
        double[][] transitionMatrix = new double[Covid19Analysis.FIELDS_WITHOUT_DATE][Covid19Analysis.FIELDS_WITHOUT_DATE];

        int countAccumulatedDataRecords = Covid19Analysis.readDataFile(covidAccumulatedDailyData, "files/exemploRegistoNumerosAcumulados.csv");
        int countTotalDataRecords = Covid19Analysis.readDataFile(covidTotalDailyData, "files/exemploRegistoNumeroTotais.csv");
        Covid19Analysis.createTransitionMatrix(transitionMatrix, "files/exemploMatrizTransicoes.txt");

        int[][] diaryCovidAccumulatedDataPeriod1 = Covid19Analysis.calculatePosition(covidAccumulatedDailyData, countAccumulatedDataRecords, "2020-04-20", "2020-04-30", true);
        int[][] diaryCovidAccumulatedDataPeriod2 = Covid19Analysis.calculatePosition(covidAccumulatedDailyData, countAccumulatedDataRecords, "2020-05-20", "2020-05-30", true);

        int[][] diaryCovidTotalDataPeriod1 = Covid19Analysis.calculatePosition(covidTotalDailyData, countTotalDataRecords, "2021-03-01", "2021-03-10", false);
        int[][] diaryCovidTotalDataPeriod2 = Covid19Analysis.calculatePosition(covidTotalDailyData, countTotalDataRecords, "2021-09-10", "2021-09-20", false);

        System.out.println("-- Testing isLeapYear --");
        System.out.println("Year 2000 test" + (testIsLeapYear(2000, true) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Year 1900 test" + (testIsLeapYear(1900, false) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Year 2020 test" + (testIsLeapYear(2020, true) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Year 2022 test" + (testIsLeapYear(2022, false) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        System.out.println("-- Testing howManyDaysTheMonthHave --");
        System.out.println("Year 2000, Month 2 test" + (testHowManyDaysTheMonthHave(2000, 2, 29) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Year 1900, Month 2 test" + (testHowManyDaysTheMonthHave(1900, 2, 28) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Year 2020, Month 2 test" + (testHowManyDaysTheMonthHave(2020, 2, 29) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Year 2000, Month 3 test" + (testHowManyDaysTheMonthHave(2000, 3, 31) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Year 2000, Month 8 test" + (testHowManyDaysTheMonthHave(2000, 8, 31) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        System.out.println("-- Testing readDataFile --");
        System.out.println("file/exemploRegistoNumerosAcumulados.csv" + (testReadDataFile("files/exemploRegistoNumerosAcumulados.csv",
                61) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("file/este ficheiro não existe.csv" + (testReadDataFile("file/este ficheiro não existe.csv",
                0) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        System.out.println("-- Testing identifyWeekDay --");
        System.out.println("Date 2022-01-09" + (testIdentifyWeekDay("2022-01-09", 1) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Date 2022-01-10" + (testIdentifyWeekDay("2022-01-10", 2) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Date 2022-01-11" + (testIdentifyWeekDay("2022-01-11", 3) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Date 2022-01-12" + (testIdentifyWeekDay("2022-01-12", 4) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Date 2022-01-13" + (testIdentifyWeekDay("2022-01-13", 5) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Date 2022-01-14" + (testIdentifyWeekDay("2022-01-14", 6) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Date 2022-01-15" + (testIdentifyWeekDay("2022-01-15", 7) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Date 2022-01-16" + (testIdentifyWeekDay("2022-01-16", 1) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Date 2022-01-17" + (testIdentifyWeekDay("2022-01-17", 2) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Date 2022-01-18" + (testIdentifyWeekDay("2022-01-18", 3) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Date 2021-12-18" + (testIdentifyWeekDay("2021-12-04", 7) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Date 2022-04-25" + (testIdentifyWeekDay("2022-04-25", 2) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        int[][] diaryCovidAccumulatedFullWeeks = Covid19Analysis.calculatePosition(covidAccumulatedDailyData, countAccumulatedDataRecords, "2020-04-01", "2020-04-30", true);
        int[][] diaryCovidAccumulatedFullWeeksExpected = Covid19Analysis.calculatePosition(covidAccumulatedDailyData, countAccumulatedDataRecords, "2020-04-06", "2020-04-26", true);

        int[][] diaryCovidFullWeeksTwo = Covid19Analysis.calculatePosition(covidTotalDailyData, countTotalDataRecords, "2020-11-05", "2022-01-01", true);
        int[][] diaryCovidFullWeeksTwoExpected = Covid19Analysis.calculatePosition(covidTotalDailyData, countTotalDataRecords, "2020-11-09", "2021-12-26", true);

        System.out.println("-- Testing showWeeklyAnalysis --");
        System.out.println("Array diaryCovidFullWeeks, 2020-04-06 - 2020-04-26" + (testShowWeeklyAnalysis(diaryCovidAccumulatedFullWeeks, diaryCovidAccumulatedFullWeeksExpected) ? OK_MESSAGE : NOT_OK_MESSAGE));
        diaryCovidAccumulatedFullWeeks = Covid19Analysis.calculatePosition(covidAccumulatedDailyData, countAccumulatedDataRecords, "2020-04-04", "2020-05-30", true);
        diaryCovidAccumulatedFullWeeksExpected = Covid19Analysis.calculatePosition(covidAccumulatedDailyData, countAccumulatedDataRecords, "2020-04-06", "2020-05-24", true);
        System.out.println("Array diaryCovidFullWeeks, 2020-04-20 - 2020-05-24" + (testShowWeeklyAnalysis(diaryCovidAccumulatedFullWeeks, diaryCovidAccumulatedFullWeeksExpected) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Array diaryCovidFullWeeks, 2020-04-20 - 2020-05-24" + (testShowWeeklyAnalysis(diaryCovidAccumulatedFullWeeks, diaryCovidAccumulatedFullWeeksExpected) ? OK_MESSAGE : NOT_OK_MESSAGE));

        System.out.println("Array diaryCovidFullWeeks, 2020-04-20 - 2020-05-24" + (testShowWeeklyAnalysis(diaryCovidFullWeeksTwo, diaryCovidFullWeeksTwoExpected) ? OK_MESSAGE : NOT_OK_MESSAGE));
        diaryCovidFullWeeksTwo = Covid19Analysis.calculatePosition(covidTotalDailyData, countTotalDataRecords, "2021-04-10", "2021-10-12", true);
        diaryCovidFullWeeksTwoExpected = Covid19Analysis.calculatePosition(covidTotalDailyData, countTotalDataRecords, "2021-04-12", "2021-10-10", true);
        System.out.println("Array diaryCovidFullWeeks, 2020-04-20 - 2020-05-24" + (testShowWeeklyAnalysis(diaryCovidFullWeeksTwo, diaryCovidFullWeeksTwoExpected) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        System.out.println("-- Testing findIndexOfFirstDayOccurrence --");
        System.out.println("Array covidTotalDailyData, procurar por segunda-feira" + (testFindIndexOfFirstDayOccurrence(covidTotalDailyData, 2, 1) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Array covidTotalDailyData, procurar por domingo" + (testFindIndexOfFirstDayOccurrence(covidTotalDailyData, 1, 0) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Array covidAccumulatedDailyData, procurar por segunda-feira" + (testFindIndexOfFirstDayOccurrence(covidAccumulatedDailyData, 2, 5) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Array covidAccumulatedDailyData, procurar por domingo" + (testFindIndexOfFirstDayOccurrence(covidAccumulatedDailyData, 1, 4) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        System.out.println("-- Testing findIndexOfLastDayOccurrence --");
        System.out.println("Segunda-feira: 2020-11-02 - Domingo:  2020-11-08" + (testFindIndexOfLastDayOccurrence(countTotalDataRecords, Covid19Analysis.TOTAL_WEEK_DAYS, 1, 427) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Terça-feira-feira: 2021-05-02 - Segunda-feira: 2022-01-03" + (testFindIndexOfLastDayOccurrence(countTotalDataRecords, Covid19Analysis.TOTAL_WEEK_DAYS, 184, 428) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Segunda-feira: 2021-12-27 - Domingo: 2022-01-02" + (testFindIndexOfLastDayOccurrence(countTotalDataRecords, Covid19Analysis.TOTAL_WEEK_DAYS, 421, 427) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Segunda-feira: 2020-04-06 - Domingo: 2020-05-31" + (testFindIndexOfLastDayOccurrence(countAccumulatedDataRecords, Covid19Analysis.TOTAL_WEEK_DAYS, 5, 60) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Segunda-feira: 2020-04-20 - Domingo: 2020-04-26" + (testFindIndexOfLastDayOccurrence(countAccumulatedDataRecords, Covid19Analysis.TOTAL_WEEK_DAYS, 19, 60) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Terça-feira: 2020-05-19 - Segunda-feira: 2020-05-25" + (testFindIndexOfLastDayOccurrence(countAccumulatedDataRecords, Covid19Analysis.TOTAL_WEEK_DAYS, 48, 54) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Segunda-feira: 2020-05-25 - Domingo: 2020-05-31" + (testFindIndexOfLastDayOccurrence(countAccumulatedDataRecords, Covid19Analysis.TOTAL_WEEK_DAYS, 54, 60) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        System.out.println("-- Testing fillString --");
        System.out.println("Character '-' Quantity 10 test" + (testFillString('-', 10, "----------") ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Character '*' Quantity 5 test" + (testFillString('*', 5, "*****") ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Character '_' Quantity 15 test" + (testFillString('_', 15, "_______________") ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Character '|' Quantity 10 test" + (testFillString('|', 20, "||||||||||||||||||||") ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        System.out.println("-- Testing validDate --");
        System.out.println("Date 2020-02-29 test" + (testValidDate("2020-02-29", true) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Date 2020-02-30 test" + (testValidDate("2020-02-30", false) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Date 2021-03-10 test" + (testValidDate("2021-03-10", true) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Date 29-03-2021 test" + (testValidDate("29-03-2021", false) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Date 2021-12-20 test" + (testValidDate("2021-12-20", true) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Date YYYY-13-10 test" + (testValidDate("YYYY-13-10", false) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        System.out.println("-- Testing correctlyFormattedDate --");
        System.out.println("Date 18-02-2020 test" + (testCorrectlyFormattedDate("18-02-2020", "2020-02-18") ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Date 2020-02-23 test" + (testCorrectlyFormattedDate("2020-02-23", "2020-02-23") ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Date 21-04-2021 test" + (testCorrectlyFormattedDate("21-04-2021", "2021-04-21") ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Date 2021-09-27 test" + (testCorrectlyFormattedDate("2021-09-27", "2021-09-27") ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        System.out.println("-- Testing countNumberDaysBetweenTwoDates --");
        System.out.println("Date 2020-04-01 and 2020-05-31 test" + (testCountNumberDaysBetweenTwoDates("2020-04-01", "2020-05-31", 60) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Date 2020-06-23 and 2020-06-23 test" + (testCountNumberDaysBetweenTwoDates("2020-06-23", "2020-06-23", 0) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Date 2021-09-13 and 2022-01-03 test" + (testCountNumberDaysBetweenTwoDates("2021-09-13", "2022-01-03", 112) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Date 2021-12-05 and 2021-12-25 test" + (testCountNumberDaysBetweenTwoDates("2021-12-05", "2021-12-25", 20) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        System.out.println("-- Testing comparisonBetweenTwoDates --");
        System.out.println("Date 2020-04-01 and 2020-05-31 test" + (testComparisonBetweenTwoDates("2020-04-01", "2020-05-31", 1) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Date 2020-06-23 and 2020-06-23 test" + (testComparisonBetweenTwoDates("2020-06-23", "2020-06-23", 0) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Date 2022-01-03 and 2021-09-13 test" + (testComparisonBetweenTwoDates("2022-01-03", "2021-09-13", -1) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Date 2021-12-05 and 2021-12-25 test" + (testComparisonBetweenTwoDates("2021-12-05", "2021-12-25", 1) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        System.out.println("-- Testing calendarToString --");
        System.out.println("Year 2000, Month 2, Day 1 test" + (testCalendarToString(2020, 2, 1, "2020-02-01") ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Year 2010, Month 5, Day 10 test" + (testCalendarToString(2010, 5, 10, "2010-05-10") ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Year 2015, Month 8, Day 19 test" + (testCalendarToString(2015, 8, 19, "2015-08-19") ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Year 2020, Month 11, Day 26 test" + (testCalendarToString(2020, 11, 26, "2020-11-26") ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        System.out.println("-- Testing addDayData --");
        System.out.println("Date 2020-02-01 Add 3 days test" + (testAddDayData("2020-02-01", 3, "2020-02-04") ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Date 2021-08-29 Add 5 days test" + (testAddDayData("2021-08-29", 5, "2021-09-03") ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Date 2021-09-01 Add 60 days test" + (testAddDayData("2021-09-01", 60, "2021-10-31") ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Date 2021-11-10 Add 90 days test" + (testAddDayData("2021-11-10", 90, "2022-02-08") ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        System.out.println("-- Testing calculateDifferenceBetweenTwoNumbers --");
        System.out.println("Numbers 100 and 200 test" + (testCalculateDifferenceBetweenTwoNumbers(100, 200, 100) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Numbers 125 and 350 test" + (testCalculateDifferenceBetweenTwoNumbers(125, 300, 175) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Numbers 190 and 710 test" + (testCalculateDifferenceBetweenTwoNumbers(190, 710, 520) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Numbers 231 and 983 test" + (testCalculateDifferenceBetweenTwoNumbers(231, 983, 752) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        System.out.println("-- Testing FindNextMonthIndex --");
        System.out.println("Index 20 -> expect 30 test" + (testFindNextMonthIndex(covidTotalDailyData, 20, 30) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Index 1 -> expect 30 test" + (testFindNextMonthIndex(covidTotalDailyData, 1, 30) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Index 35 -> expect 61 test" + (testFindNextMonthIndex(covidTotalDailyData, 35, 61) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Index 50 -> expect 61 test" + (testFindNextMonthIndex(covidTotalDailyData, 50, 61) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Index 61 -> expect 92 test" + (testFindNextMonthIndex(covidTotalDailyData, 61, 92) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Index 75 -> expect 92 test" + (testFindNextMonthIndex(covidTotalDailyData, 75, 92) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        System.out.println("-- Testing itsFirstDayAndHaveFullMonth --");
        System.out.println("starting day 0 -> expect true" + (testItsFirstDayAndHaveFullMonth(covidTotalDailyData, 0, countTotalDataRecords, true) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("starting day 10 -> expect false" + (testItsFirstDayAndHaveFullMonth(covidTotalDailyData, 10, countTotalDataRecords, false) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("starting day 91 -> expect false" + (testItsFirstDayAndHaveFullMonth(covidTotalDailyData, 91, countTotalDataRecords, false) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("starting day 92 -> expect true" + (testItsFirstDayAndHaveFullMonth(covidTotalDailyData, 92, countTotalDataRecords, true) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("starting day 93 -> expect false" + (testItsFirstDayAndHaveFullMonth(covidTotalDailyData, 93, countTotalDataRecords, false) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("starting day 9999 -> expect false" + (testItsFirstDayAndHaveFullMonth(covidTotalDailyData, 9999, countTotalDataRecords, false) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("starting day -1 -> expect false" + (testItsFirstDayAndHaveFullMonth(covidTotalDailyData, -1, countTotalDataRecords, false) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        System.out.println("-- Testing testFindIndexOfTheFirstDayOfTheFirstFullMonth --");
        System.out.println("Array covidTotalDailyData --> Expected 0" + (testFindIndexOfTheFirstDayOfTheFirstFullMonth(covidTotalDailyData, countTotalDataRecords, 0) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Array covidAccumulatedDailyData --> Expected 0" + (testFindIndexOfTheFirstDayOfTheFirstFullMonth(covidAccumulatedDailyData, countAccumulatedDataRecords, 0) ? OK_MESSAGE : NOT_OK_MESSAGE));
        int[][] covidAccumulatedDailyLessData = new int[Covid19Analysis.MAXIMUM_RECORDS][Covid19Analysis.TOTAL_FIELDS];
        int countAccumulatedLessDataRecords = Covid19Analysis.readDataFile(covidAccumulatedDailyLessData, "files/exemploRegistoNumerosAcumuladosMenosDados.csv");
        int[][] covidAccumulatedDailyLessDataTwo = new int[Covid19Analysis.MAXIMUM_RECORDS][Covid19Analysis.TOTAL_FIELDS];
        int countAccumulatedLessDataRecordsTwo = Covid19Analysis.readDataFile(covidAccumulatedDailyLessDataTwo, "files/exemploRegistoNumerosAcumuladosMenosDadosDois.csv");
        System.out.println("Array covidAccumulatedDailyLessData --> Expected 21" + (testFindIndexOfTheFirstDayOfTheFirstFullMonth(covidAccumulatedDailyLessData, countAccumulatedLessDataRecords, 21) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Array covidAccumulatedDailyLessData --> Expected -1" + (testFindIndexOfTheFirstDayOfTheFirstFullMonth(covidAccumulatedDailyLessDataTwo, countAccumulatedLessDataRecordsTwo, -1) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        System.out.println("-- Testing findIndexOfTheLastDayOfTheLastFullMonth --");
        System.out.println("Array covidTotalDailyData --> Expected 425" + (testFindIndexOfTheLastDayOfTheLastFullMonth(covidTotalDailyData, countTotalDataRecords, 425) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Array covidAccumulatedDailyData --> Expected 60" + (testFindIndexOfTheLastDayOfTheLastFullMonth(covidAccumulatedDailyData, countAccumulatedDataRecords, 60) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        System.out.println("-- Testing testCountFullMonthsOnAInterval --");
        System.out.println("Array covidTotalDailyData --> Expected 14" + (testCountFullMonthsOnAInterval(covidTotalDailyData, countTotalDataRecords, 14) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Array covidAccumulatedDailyData --> Expected 2" + (testCountFullMonthsOnAInterval(covidAccumulatedDailyData, countAccumulatedDataRecords, 2) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        System.out.println("-- Testing fileAlreadyExists --");
        System.out.println("file -> files/exemploRegistoNumerosAcumulados.csv | expect true" + (testFileAlreadyExists("files/exemploRegistoNumerosAcumulados.csv", true) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("file -> este ficheiro não existe.csv | expect false" + (testFileAlreadyExists("este ficheiro não existe.csv", false) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        int[][] matrixCalculatePositionAccumulatedData1 = {
                {2020, 4, 10, 775, 1516, 1179, 226, 26},
                {2020, 4, 11, 843, 515, 1175, 233, 35},
                {2020, 4, 12, 632, 598, 1177, 228, 34},
                {2020, 4, 13, 746, 349, 1187, 188, 31},
                {2020, 4, 14, 447, 514, 1227, 218, 32},
                {2020, 4, 15, 705, 643, 1200, 208, 32}
        };

        int[][] matrixCalculatePositionAccumulatedData2 = {
                {2020, 5, 20, 232, 228, 609, 93, 16},
                {2020, 5, 21, 217, 252, 608, 92, 14},
                {2020, 5, 22, 185, 288, 576, 84, 12},
                {2020, 5, 23, 261, 271, 550, 80, 13}
        };

        int[][] matrixCalculatePositionTotalData1 = {
                {2021, 8, 20, 10104555, 44916, 687, 143, 9},
                {2021, 8, 21, 10104879, 44588, 681, 145, 8},
                {2021, 8, 22, 10103959, 45465, 708, 152, 9},
                {2021, 8, 23, 10103852, 45542, 733, 151, 6},
                {2021, 8, 24, 10105221, 44180, 716, 148, 13},
                {2021, 8, 25, 10104427, 44990, 688, 144, 16}
        };

        int[][] matrixCalculatePositionTotalData2 = {
                {2022, 1, 1, 9951559, 196223, 1023, 142, 21},
                {2022, 1, 2, 9944382, 203322, 1081, 148, 14},
                {2022, 1, 3, 9939750, 207859, 1167, 147, 10}
        };

        System.out.println("-- Testing calculatePosition --");
        System.out.println("Accumulated Data Date 2020-04-10 to 2020-04-15 test" + (testCalculatePosition(covidAccumulatedDailyData, countAccumulatedDataRecords, "2020-04-10", "2020-04-15", true, matrixCalculatePositionAccumulatedData1) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Accumulated Data Date 2020-05-20 to 2020-05-23 test" + (testCalculatePosition(covidAccumulatedDailyData, countAccumulatedDataRecords, "2020-05-20", "2020-05-23", true, matrixCalculatePositionAccumulatedData2) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Total Data Date 2021-08-20 to 2021-08-25 test" + (testCalculatePosition(covidTotalDailyData, countTotalDataRecords, "2021-08-20", "2021-08-25", false, matrixCalculatePositionTotalData1) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Total Data Date 2022-01-01 to 2022-01-03 test" + (testCalculatePosition(covidTotalDailyData, countTotalDataRecords, "2022-01-01", "2022-01-03", false, matrixCalculatePositionTotalData2) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        System.out.println("-- Testing countNumberDaysWithDataBetweenTwoDates --");
        System.out.println("Accumulated Data Date 2020-04-01 and 2020-04-01 test" + (testCountNumberDaysWithDataBetweenTwoDates(covidAccumulatedDailyData, countAccumulatedDataRecords, "2020-04-01", "2020-04-01", true, 0) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Accumulated Data Date 2020-04-01 and 2020-05-31 test" + (testCountNumberDaysWithDataBetweenTwoDates(covidAccumulatedDailyData, countAccumulatedDataRecords, "2020-04-01", "2020-05-31", true, 60) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Total Data Date 2020-09-01 and 2020-12-31 test" + (testCountNumberDaysWithDataBetweenTwoDates(covidTotalDailyData, countTotalDataRecords, "2020-09-01", "2020-12-31", false, 61) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Total Data Date 2022-01-01 and 2022-01-10 test" + (testCountNumberDaysWithDataBetweenTwoDates(covidTotalDailyData, countTotalDataRecords, "2022-01-01", "2022-01-10", false, 7) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        int[][] diaryCovidFullInterval = Covid19Analysis.calculatePosition(covidAccumulatedDailyData, countAccumulatedDataRecords, "2020-04-01", "2020-05-31", true);
        int[][] diaryCovidFullMonth = Covid19Analysis.calculatePosition(covidAccumulatedDailyData, countAccumulatedDataRecords, "2020-05-01", "2020-05-31", true);

        int[][] diaryCovidFullIntervalTwo = Covid19Analysis.calculatePosition(covidTotalDailyData, countTotalDataRecords, "2021-02-19", "2021-05-10", true);
        int[][] diaryCovidFullMonthTwo = Covid19Analysis.calculatePosition(covidTotalDailyData, countTotalDataRecords, "2021-03-01", "2021-04-30", true);

        System.out.println("-- Testing buildArrayWithFullMonthlyIntervals --");
        System.out.println("Testing full month on diaryCovidFullInterval: 2020-05-01 - 2020-05-31" + (testBuildArrayWithFullMonthlyIntervals(diaryCovidFullInterval, diaryCovidFullMonth) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Testing full month on diaryCovidFullIntervalTwo: 2021-03-01 - 2020-04-30" + (testBuildArrayWithFullMonthlyIntervals(diaryCovidFullIntervalTwo, diaryCovidFullMonthTwo) ? OK_MESSAGE : NOT_OK_MESSAGE));
        diaryCovidFullIntervalTwo = Covid19Analysis.calculatePosition(covidTotalDailyData, countTotalDataRecords, "2021-08-02", "2022-01-29", true);
        diaryCovidFullMonthTwo = Covid19Analysis.calculatePosition(covidTotalDailyData, countTotalDataRecords, "2021-09-01", "2021-12-31", true);
        System.out.println("Testing full month on diaryCovidFullIntervalTwo: 2021-09-01 - 2020-12-31" + (testBuildArrayWithFullMonthlyIntervals(diaryCovidFullIntervalTwo, diaryCovidFullMonthTwo) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        int[][] expectedResultOne = {{2020, 5, 1, 8404, 7455, 20984, 3226, 421}};
        int[][] expectedResultTwo = {
                {2021, 9, 1, 303397173, 1085266, 15741, 3152, 232},
                {2021, 10, 1, 313687269, 946168, 9881, 1844, 182},
                {2021, 11, 1, 303253855, 1217559, 15396, 2396, 284},
                {2021, 12, 1, 312071446, 2520642, 28846, 4442, 514},
        };

        diaryCovidFullMonthTwo = Covid19Analysis.calculatePosition(covidTotalDailyData, countTotalDataRecords, "2021-09-01", "2021-12-31", false);

        System.out.println("-- Testing calculateSumOfEveryDayStatistics --");
        System.out.println("covidAccumulatedDailyData 2020-05-01 - 2020-05-31 test" + (testCalculateSumOfEveryDayStatistics(diaryCovidFullMonth, 1, Covid19Analysis.MONTHLY_TYPE, expectedResultOne) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("covidTotalDailyData 2021-09-01 - 2021-12-31 test" + (testCalculateSumOfEveryDayStatistics(diaryCovidFullMonthTwo, 4, Covid19Analysis.MONTHLY_TYPE, expectedResultTwo) ? OK_MESSAGE : NOT_OK_MESSAGE));

        int[][] expectedResultWeeklyTypeAccumulated = {
                {2020, 5, 4, 1780, 2299, 5797, 907, 92},
                {2020, 5, 11, 2194, 1455, 4865, 771, 83},
                {2020, 5, 18, 1390, 1587, 4136, 633, 98},
                {2020, 5, 25, 1644, 1877, 3583, 467, 94}
        };

        int[][] expectedResultWeeklyTypeTotals = {
                {2021, 9, 6, 70770524, 274956, 4240, 896, 63},
                {2021, 9, 13, 70799362, 246610, 3547, 730, 46},
                {2021, 9, 20, 70820904, 225442, 2997, 555, 47},
                {2021, 9, 27, 70832485, 214063, 2607, 488, 39},
                {2021, 10, 4, 70836258, 210275, 2386, 409, 48},
                {2021, 10, 11, 70835574, 210747, 2238, 399, 56},
                {2021, 10, 18, 70831998, 214227, 2012, 405, 36},
                {2021, 10, 25, 70825990, 219794, 2209, 430, 24},
                {2021, 11, 1, 70817251, 227986, 2482, 451, 46},
                {2021, 11, 8, 70796155, 248453, 2784, 457, 54},
                {2021, 11, 15, 70755383, 287809, 3662, 559, 64},
                {2021, 11, 22, 70695899, 345372, 4826, 702, 96},
                {2021, 11, 29, 70641173, 397990, 6091, 864, 120},
                {2021, 12, 6, 70588648, 448969, 6625, 970, 121},
                {2021, 12, 13, 70550974, 485762, 6645, 1033, 120},
                {2021, 12, 20, 70433747, 602549, 6248, 1060, 96}
        };

        int[][] dateIntervalWeeklyAccumulated = Covid19Analysis.calculatePosition(covidAccumulatedDailyData, countAccumulatedDataRecords, "2020-05-04", "2020-05-31", true);
        int[][] dateIntervalWeeklyTotal = Covid19Analysis.calculatePosition(covidTotalDailyData, countTotalDataRecords, "2021-09-06", "2021-12-26", false);

        System.out.println("covidAccumulatedDailyData 2020-05-04 - 2020-05-31 test" + (testCalculateSumOfEveryDayStatistics(dateIntervalWeeklyAccumulated, 4, Covid19Analysis.WEEKLY_TYPE, expectedResultWeeklyTypeAccumulated) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("covidTotalDailyData 2021-09-06 - 2021-12-26 test" + (testCalculateSumOfEveryDayStatistics(dateIntervalWeeklyTotal, 16, Covid19Analysis.WEEKLY_TYPE, expectedResultWeeklyTypeTotals) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        int[] matrixSumValuesAccumulatedData1 = {5936, 4839, 11613, 2084, 275};
        int[] matrixSumValuesAccumulatedData2 = {2496, 2771, 5988, 830, 149};
        int[] matrixSumValuesTotalData1 = {100864883, 629644, 15994, 3766, 300};
        int[] matrixSumValuesTotalData2 = {111249171, 394420, 5708, 1171, 78};

        System.out.println("-- Testing sumValues --");
        System.out.println("Accumulated Data Period between 2020-04-20 and 2020-04-30 test" + (testSumValues(diaryCovidAccumulatedDataPeriod1, matrixSumValuesAccumulatedData1) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Accumulated Data Period between 2020-05-20 and 2020-05-30 test" + (testSumValues(diaryCovidAccumulatedDataPeriod2, matrixSumValuesAccumulatedData2) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Total Data Period between 2021-03-01 and 2021-03-10 test" + (testSumValues(diaryCovidTotalDataPeriod1, matrixSumValuesTotalData1) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Total Data Period between 2021-09-10 and 2021-09-20 test" + (testSumValues(diaryCovidTotalDataPeriod2, matrixSumValuesTotalData2) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        int[][] matrixCalculateDifferenceAccumulatedData = {
                {0, 0, 0, 360, 429, 599, 122, 5},
                {0, 0, 0, 129, 264, 564, 121, 13},
                {0, 0, 0, 324, 315, 570, 123, 11},
                {0, 0, 0, 376, 100, 545, 124, 22},
                {0, 0, 0, 518, 292, 532, 110, 20},
                {0, 0, 0, -45, 430, 509, 114, 12},
                {0, 0, 0, 485, 253, 492, 111, 11},
                {0, 0, 0, 295, -122, 485, 110, 11},
                {0, 0, 0, 424, -9, 424, 107, 7},
                {0, 0, 0, 262, -167, 451, 103, 11},
                {0, 0, 0, 312, 283, 454, 109, 3}
        };

        int[][] matrixCalculateDifferenceTotalData = {
                {0, 0, 0, -30207, 29750, 1598, 351, 27},
                {0, 0, 0, -28362, 28056, 1445, 325, 28},
                {0, 0, 0, -27100, 26978, 1258, 295, 33},
                {0, 0, 0, -26574, 26580, 1122, 280, 23},
                {0, 0, 0, -27184, 27271, 1032, 267, 22},
                {0, 0, 0, -26522, 26759, 889, 244, 16},
                {0, 0, 0, -26642, 26822, 917, 251, 22},
                {0, 0, 0, -26766, 26922, 929, 245, 18},
                {0, 0, 0, -26277, 26541, 821, 222, 23},
                {0, 0, 0, -22832, 23179, 746, 197, 17}
        };

        System.out.println("-- Testing calculateDifference --");
        System.out.println("Accumulated Data Period between 2020-04-20 to 2020-04-30 and 2020-05-20 to 2020-05-30 test" + (testCalculateDifference(diaryCovidAccumulatedDataPeriod1, diaryCovidAccumulatedDataPeriod2, matrixCalculateDifferenceAccumulatedData) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Total Data Period between 2021-03-01 to 2021-03-10 and 2021-09-10 to 2021-09-20 test" + (testCalculateDifference(diaryCovidTotalDataPeriod1, diaryCovidTotalDataPeriod2, matrixCalculateDifferenceTotalData) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        double[] matrixCalculateAverageAccumulatedData1 = {539.6363636363636, 439.90909090909093, 1055.7272727272727, 189.45454545454547, 25.0};
        double[] matrixCalculateAverageAccumulatedData2 = {226.9090909090909, 251.9090909090909, 544.3636363636364, 75.45454545454545, 13.545454545454545};
        double[] matrixCalculateAverageTotalData1 = {10086488.3, 62964.4, 1599.4, 376.6, 30.0};
        double[] matrixCalculateAverageTotalData2 = {10113561, 35856.36363636364, 518.9090909090909, 106.45454545454545, 7.090909090909091};

        System.out.println("-- Testing calculateAverage --");
        System.out.println("Accumulated Data Period between 2020-04-20 and 2020-04-30 test" + (testCalculateAverage(diaryCovidAccumulatedDataPeriod1, matrixCalculateAverageAccumulatedData1) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Accumulated Data Period between 2020-05-20 and 2020-05-30 test" + (testCalculateAverage(diaryCovidAccumulatedDataPeriod2, matrixCalculateAverageAccumulatedData2) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Total Data Period between 2021-03-01 and 2021-03-10 test" + (testCalculateAverage(diaryCovidTotalDataPeriod1, matrixCalculateAverageTotalData1) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Total Data Period between 2021-09-10 and 2021-09-20 test" + (testCalculateAverage(diaryCovidTotalDataPeriod2, matrixCalculateAverageTotalData2) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        double[] matrixCalculateStandardDeviationAccumulatedData1 = {151.49573815023956, 160.41506081268426, 85.71739073478709, 16.527462745182287, 5.359782899266791};
        double[] matrixCalculateStandardDeviationAccumulatedData2 = {30.269860634134368, 55.85118952986433, 35.520218895908044, 10.254609928875327, 1.0756508696544758};
        double[] matrixCalculateStandardDeviationTotalData1 = {3177.017785597053, 2914.1883329668317, 302.1129590070575, 54.9530708878039, 5.639148871948674};
        double[] matrixCalculateStandardDeviationTotalData2 = {1728.0300397220583, 1692.8840519135208, 47.115823525077865, 14.531243057405256, 1.6211413181933645};

        System.out.println("-- Testing calculateStandardDeviation --");
        System.out.println("Accumulated Data Period between 2020-04-20 and 2020-04-30 test" + (testCalculateStandardDeviation(diaryCovidAccumulatedDataPeriod1, matrixCalculateStandardDeviationAccumulatedData1) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Accumulated Data Period between 2020-05-20 and 2020-05-30 test" + (testCalculateStandardDeviation(diaryCovidAccumulatedDataPeriod2, matrixCalculateStandardDeviationAccumulatedData2) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Total Data Period between 2021-03-01 and 2021-03-10 test" + (testCalculateStandardDeviation(diaryCovidTotalDataPeriod1, matrixCalculateStandardDeviationTotalData1) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Total Data Period between 2021-09-10 and 2021-09-20 test" + (testCalculateStandardDeviation(diaryCovidTotalDataPeriod2, matrixCalculateStandardDeviationTotalData2) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        double[][] transitionMatrixExpected = {
                {0.9995, 0.03, 0.002, 0.001, 0},
                {0.0005, 0.96, 0.004, 0.015, 0},
                {0, 0.007, 0.98, 0.02, 0},
                {0, 0.003, 0.01, 0.95, 0},
                {0, 0, 0.004, 0.014, 1}
        };

        double[][] transitionMatrixOriginal = new double[Covid19Analysis.FIELDS_WITHOUT_DATE][Covid19Analysis.FIELDS_WITHOUT_DATE];

        System.out.println("-- Testing createTransitionMatrix --");
        System.out.println("Create TransitionMatrix test" + (testCreateTransitionMatrix(transitionMatrixOriginal, "files/exemploMatrizTransicoes.txt", true) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Compare TransitionMatrix test" + (compareArraysDouble(transitionMatrixOriginal, transitionMatrixExpected) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Try to create TransitionMatrix that doesn't exists test" + (testCreateTransitionMatrix(transitionMatrixOriginal, "files/esta matriz nao existe.txt", false) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        System.out.println("-- Testing createTransitionMatrixWithoutAbsorbingState --");
        double[][] matrixToRemoveLineColumn = {
                {2, 3, 4, 5},
                {2, 3, 4, 5},
                {2, 3, 4, 5},
                {2, 3, 4, 5}
        };
        double[][] matrixToRemoveLineColumnExpectedResult = {
                {2, 3, 4},
                {2, 3, 4},
                {2, 3, 4}
        };
        System.out.println("Teste 1:" + (testCreateTransitionMatrixWithoutAbsorbingState(matrixToRemoveLineColumn, matrixToRemoveLineColumnExpectedResult) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Teste 2:" + (testCreateTransitionMatrixWithoutAbsorbingState(null, null) ? OK_MESSAGE : NOT_OK_MESSAGE));
        double[][] matrixToRemoveLineColumnTwo = {
                {2, 3},
                {4, 5}
        };
        System.out.println("Teste 3:" + (testCreateTransitionMatrixWithoutAbsorbingState(matrixToRemoveLineColumnTwo, null) ? OK_MESSAGE : NOT_OK_MESSAGE));
        double[][] matrixToRemoveLineColumnExpectedResultThree = {{0.9995, 0.03, 0.002, 0.001}, {0.0005, 0.96, 0.004, 0.015}, {0, 0.007, 0.98, 0.02}, {0, 0.003, 0.01, 0.95}};
        System.out.println("Teste 4:" + (testCreateTransitionMatrixWithoutAbsorbingState(transitionMatrix, matrixToRemoveLineColumnExpectedResultThree) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        int[] matrixLastDayDataOne = {2021, 2, 28, 10079689, 69268, 2165, 484, 41};
        int[] matrixLastDayDataTwo = {2021, 10, 22, 10118649, 30805, 284, 60, 8};
        int[] matrixLastDayDataThree = {2022, 1, 7, 9893098, 254240, 1353, 161, 17};

        System.out.println("-- Testing lastDayData --");
        System.out.println("Forecast Date 2020-11-01 test" + (testLastDayData(covidTotalDailyData, countTotalDataRecords, "2020-11-01", null) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Forecast Date 2021-03-01 test" + (testLastDayData(covidTotalDailyData, countTotalDataRecords, "2021-03-01", matrixLastDayDataOne) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Forecast Date 2021-10-23 test" + (testLastDayData(covidTotalDailyData, countTotalDataRecords, "2021-10-23", matrixLastDayDataTwo) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Forecast Date 2022-01-08 test" + (testLastDayData(covidTotalDailyData, countTotalDataRecords, "2022-01-08", matrixLastDayDataThree) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        System.out.println("-- Testing calculateTheForecastForTheNextDay --");
        double[] forecastValues = {2, 4, 6, 8};
        double[][] forecastMatrixValues = {
                {2, 3, 7, 1},
                {2, 4, 9, 2},
                {2, 5, 10, 3},
                {2, 6, 20, 4}
        };
        double[] forecastExpectedResult = {66, 90, 108, 180};
        System.out.println("Expected [66, 90, 108, 180]" + (testCalculateTheForecastForTheNextDay(forecastValues, forecastMatrixValues, forecastExpectedResult) ? OK_MESSAGE : NOT_OK_MESSAGE));
        double[] forecastValuesTwo = {9969087, 178712, 1024, 145, 18};
        double[] forecastExpectedResultTwo = {9969466.0095, 176554.33449999997, 2257.404, 684.126, 24.126};
        System.out.println("Expected [9969466.0095, 176554.33449999997, 2257.404, 684.126, 24.126]" + (testCalculateTheForecastForTheNextDay(forecastValuesTwo, transitionMatrix, forecastExpectedResultTwo) ? OK_MESSAGE : NOT_OK_MESSAGE));

        double[] forecastValuesThree = {2, 4, 5, 9, 10};
        double[][] forecastMatrixValuesThree = {
                {90, 20, 5, 0.5, 0.002},
                {0.2, 29, 0.25, 0.9, 0.85},
                {9, 10, 19, 10, 18.5},
                {12, 5.5, 11.2, 12, 1.9},
                {20, 9.5, 0.99, 9.59, 2.5}
        };
        double[] forecastExpectedResultThree = {289.52, 134.25, 428, 229, 194.26};
        System.out.println("Expected [289.52, 134.25, 428, 229, 194.26]" + (testCalculateTheForecastForTheNextDay(forecastValuesThree, forecastMatrixValuesThree, forecastExpectedResultThree) ? OK_MESSAGE : NOT_OK_MESSAGE));

        System.out.println(SEPARATOR);

        int[] lastDayData = {2022, 1, 7, 9893098, 254240, 1353, 161, 17};

        double[] matrixPredictCovidValues1 = {9895781.518000001, 249024.776, 3108.8399999999997, 929.2, 24.666};
        double[] matrixPredictCovidValues2 = {9927524.326930704, 156138.99934123864, 41700.96069811738, 14629.627095604053, 8875.085934354864};
        double[] matrixPredictCovidValues3 = {9851099.952282432, 137625.1343215776, 66487.30996020438, 21228.258735769323, 72428.34470010622};
        double[] matrixPredictCovidValues4 = {9771784.29844949, 137584.68878461182, 69942.75055989865, 22198.908145856483, 147358.35406029003};

        System.out.println("-- Testing predictCovidValues --");
        System.out.println("Forecast Date 2022-01-08 test" + (testPredictCovidValues(lastDayData, transitionMatrix, "2022-01-08", matrixPredictCovidValues1) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Forecast Date 2022-02-15 test" + (testPredictCovidValues(lastDayData, transitionMatrix, "2022-02-15", matrixPredictCovidValues2) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Forecast Date 2022-06-23 test" + (testPredictCovidValues(lastDayData, transitionMatrix, "2022-06-23", matrixPredictCovidValues3) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("Forecast Date 2022-10-30 test" + (testPredictCovidValues(lastDayData, transitionMatrix, "2022-10-30", matrixPredictCovidValues4) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        System.out.println("-- Testing theFinalCountdown--");
        double[] finalCountdownResult = {16809.24855492833, 14809.24855492648, 8865.317919082967, 8345.086705209691};
        System.out.println("Teste matriz transição 1:" + (testTheFinalCountdown(transitionMatrix, finalCountdownResult) ? OK_MESSAGE : NOT_OK_MESSAGE));
        double[][] finalCountdownTransitionMatrix = {{0.9995, 0.049, 0.005, 0.001, 0}, {0.0005, 0.95, 0.005, 0.018, 0}, {0, 0.0009, 0.98, 0.02, 0}, {0, 0.0001, 0.0085, 0.947, 0}, {0, 0, 0.0015, 0.014, 1}};
        double[] finalCountdownResultTwo = {425017.64847148827, 423017.64847144135, 329354.1617056339, 275989.0293409452};
        System.out.println("Teste matriz transição 2:" + (testTheFinalCountdown(finalCountdownTransitionMatrix, finalCountdownResultTwo) ? OK_MESSAGE : NOT_OK_MESSAGE));
        double[][] finalCountdownTransitionMatrixTwo = {{0.85, 0.10, 0.04, 0.01, 0}, {0.04, 0.90, 0.03, 0.02, 0}, {0.02, 0.03, 0.80, 0.10, 0}, {0.01, 0.03, 0.06, 0.80, 0}, {0, 0.01, 0.05, 0.10, 1}};
        double[] finalCountdownResultThree = {24.354169185507615, 48.510861242090876, 24.08011929230647, 23.10885422963771};
        System.out.println("Teste matriz transição 3:" + (testTheFinalCountdown(finalCountdownTransitionMatrixTwo, finalCountdownResultThree) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        double[][] matrix1 = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        double[][] matrix2 = {{1, 2, 3, 4}, {4, 5, 6, 7}, {7, 8, 9, 1}, {1, 2, 3, 4}};
        double[][] matrix3 = {{1, 2, 3, 4}, {2, 3, 4, 5}, {3, 4, 5, 6}, {4, 5, 6, 7}};
        double[][] matrix4 = {{6, 0, 3, 0, 2, 6, 0, 3, 9, 4},
                {9, 6, 7, 4, 1, 3, 7, 5, 1, 5},
                {5, 8, 9, 2, 6, 3, 8, 2, 2, 4},
                {1, 5, 8, 4, 5, 5, 6, 9, 1, 6},
                {1, 5, 7, 6, 6, 4, 0, 7, 3, 7},
                {7, 2, 7, 9, 0, 7, 8, 3, 9, 0},
                {4, 9, 2, 6, 0, 6, 9, 6, 8, 9},
                {3, 0, 7, 9, 5, 3, 9, 7, 2, 2},
                {6, 4, 8, 9, 3, 9, 7, 8, 5, 8},
                {5, 5, 6, 4, 7, 7, 8, 5, 3, 2}};
        double[][] matrix5 = {{3, 4, 6, 7, 8, 5, 0, 1, 8, 7},
                {5, 4, 9, 5, 4, 1, 6, 1, 2, 3},
                {1, 5, 8, 5, 0, 1, 9, 4, 7, 1},
                {8, 3, 1, 1, 0, 8, 7, 7, 3, 3},
                {7, 7, 9, 0, 9, 8, 9, 4, 7, 4},
                {8, 9, 6, 3, 4, 3, 8, 4, 6, 7},
                {0, 4, 0, 0, 9, 7, 9, 5, 7, 7},
                {0, 5, 3, 5, 1, 4, 8, 6, 1, 1},
                {6, 9, 8, 7, 8, 2, 0, 9, 2, 6},
                {4, 8, 2, 3, 0, 7, 9, 1, 4, 3}};
        double[][] matrix6 = {{1.2, 2.3, 3.4}, {4.5, 5.6, 6.7}, {7.8, 8.9, 10}};
        double[][] matrix7 = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        double[][] subtractTwoMatricesExpectedResult1 = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
        double[][] subtractTwoMatricesExpectedResult2 = {{0, 0, 0, 0}, {2, 2, 2, 2}, {4, 4, 4, -5}, {-3, -3, -3, -3}};
        double[][] subtractTwoMatricesExpectedResult3 = {{3, -4, -3, -7, -6, 1, 0, 2, 1, -3},
                {4, 2, -2, -1, -3, 2, 1, 4, -1, 2},
                {4, 3, 1, -3, 6, 2, -1, -2, -5, 3,},
                {-7, 2, 7, 3, 5, -3, -1, 2, -2, 3},
                {-6, -2, -2, 6, -3, -4, -9, 3, -4, 3},
                {-1, -7, 1, 6, -4, 4, 0, -1, 3, -7},
                {4, 5, 2, 6, -9, -1, 0, 1, 1, 2},
                {3, -5, 4, 4, 4, -1, 1, 1, 1, 1},
                {0, -5, 0, 2, -5, 7, 7, -1, 3, 2},
                {1, -3, 4, 1, 7, 0, -1, 4, -1, -1}};
        double[][] subtractTwoMatricesExpectedResult4 = {{0.19999999999999996, 0.2999999999999998, 0.3999999999999999},
                {0.5, 0.5999999999999996, 0.7000000000000002},
                {0.7999999999999998, 0.9000000000000004, 1.0}};


        System.out.println("-- Testing subtractTwoMatrices --");
        System.out.println("matriz1: {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}} - matriz1: {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}" + (testSubtractTwoMatrices(matrix1, matrix1, subtractTwoMatricesExpectedResult1) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("matriz2: {{1, 2, 3, 4}, {4, 5, 6, 7}, {7, 8, 9, 1}, {1, 2, 3, 4}} - matrix3: {{1, 2, 3, 4}, {2, 3, 4, 5}, {3, 4, 5, 6}, {4, 5, 6, 7}}" + (testSubtractTwoMatrices(matrix2, matrix3, subtractTwoMatricesExpectedResult2) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("matriz4 - matrix5" + (testSubtractTwoMatrices(matrix4, matrix5, subtractTwoMatricesExpectedResult3) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("matriz6: {{1.2,2.3,3.4},{4.5,5.6,6.7},{7.8,8.9,10}} - matrix7: {{1,2,3},{4,5,6},{7,8,9}}" + (testSubtractTwoMatrices(matrix6, matrix7, subtractTwoMatricesExpectedResult4) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        double[][] identityMatrixExpectedResult1 = {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}};
        double[][] identityMatrixExpectedResult2 = {{1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1}};

        System.out.println("-- Testing createIdentityMatrix --");
        System.out.println("matriz order : 3" + (testCreateIdentityMatrix(3, identityMatrixExpectedResult1) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("matriz order : 4" + (testCreateIdentityMatrix(4, identityMatrixExpectedResult2) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        double[][] matrix11 = {{1, -2, 2, 2}, {0, 4, -2, 1}, {1, -2, 4, 0}, {1, -1, 2, 2}};
        double[][] matrix12 = {{1, -2, 7, -3}, {0, 1, -4, -2}, {2, -3, 1, 6}, {-1, -2, 3, 1}};
        double[][] matrixMultiplied1 = {{3, -14, 23, 15}, {-5, 8, -15, -19}, {9, -16, 19, 25}, {3, -13, 19, 13}};
        double[][] matrix13 = {{1, -2, 2, 2}, {0, 4, -2, 1}, {1, -2, 4, 0}, {1, -1, 2, 2}};
        double[][] matrix14 = {{3, 2, 1, 4}, {1, 4, 2, 3}, {4, 2, 3, 1}, {2, 3, 1, 4}};
        double[][] matrixMultiplied2 = {{13, 4, 5, 8}, {-2, 15, 3, 14}, {17, 2, 9, 2}, {14, 8, 7, 11}};

        System.out.println("-- Testing testMultiplicationMatrix--");
        System.out.println("matrix: {{1, -2, 2, 2}, {0, 4, -2, 1}, {1, -2, 4, 0}, {1, -1, 2, 2}},matrix:{{1, -2, 7, -3}, {0, 1, -4, -2}, {2, -3, 1, 6}, {-1, -2, 3, 1}} " + (testMultiplicationMatrix(matrix11, matrix12, matrixMultiplied1) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("matrix: {{1, -2, 2, 2}, {0, 4, -2, 1}, {1, -2, 4, 0}, {1, -1, 2, 2}},matrix:{{3, 2, 1, 4}, {1, 4, 2, 3}, {4, 2, 3, 1}, {2, 3, 1, 4}} " + (testMultiplicationMatrix(matrix13, matrix14, matrixMultiplied2) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        System.out.println("-- Testing inverseMatrix --");
        double[][] matrixToInvert = {
                {0.0004999999999999449, -0.03, -0.002, -0.001},
                {-0.0005, 0.040000000000000036, -0.004, -0.015},
                {0.0, -0.007, 0.020000000000000018, -0.02},
                {0.0, -0.003, -0.01, 0.050000000000000044},
        };
        double[][] matrixToInvertResult = {
                {16421.965317933766, 14421.965317931958, 8567.630057811111, 8082.080924862702},
                {231.2138728325765, 231.21387283255103, 132.94797687872014, 127.1676300579048},
                {118.49710982669528, 118.49710982668223, 130.6358381503439, 90.17341040467606},
                {37.57225433529361, 37.57225433528947, 34.10404624279195, 45.66473988440946}
        };
        System.out.println("Teste 1:" + (testInverseMatrix(matrixToInvert, matrixToInvertResult) ? OK_MESSAGE : NOT_OK_MESSAGE));
        double[][] matrixToInvertTwo = {
                {0.0004999999999999449, -0.049, -0.005, -0.001},
                {-0.0005, 0.050000000000000044, -0.005, -0.018},
                {0.0, -0.0009, 0.020000000000000018, -0.02},
                {0.0, -0.0001, -0.0085, 0.05300000000000005}
        };
        double[][] matrixToInvertResultTwo = {
                {420489.38708335656, 418489.3870833101, 325766.75412148464, 272993.0837172191},
                {4245.1705223966155, 4245.170522396146, 3298.354400266359, 2766.515621336873},
                {237.0617696214736, 237.0617696214474, 243.73956594745815, 176.9616026746544},
                {46.02909611362613, 46.02909611362105, 45.31361793547223, 52.468399714495234}
        };
        System.out.println("Teste 2:" + (testInverseMatrix(matrixToInvertTwo, matrixToInvertResultTwo) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        System.out.println("-- Testing luDecomposition --");
        double[][] matrixToDecompose = {
                {0.0004999999999999449, -0.03, -0.002, -0.001},
                {-0.0005, 0.040000000000000036, -0.004, -0.015},
                {0.0, -0.007, 0.020000000000000018, -0.02},
                {0.0, -0.003, -0.01, 0.050000000000000044}
        };
        double[][] matrixToDecomposeResult = {
                {0.0004999999999999449, -0.03, -0.002, -0.001},
                {-1.0000000000001101, 0.009999999999996734, -0.0060000000000002204, -0.01600000000000011},
                {0.0, -0.7000000000002287, 0.015799999999998492, -0.03120000000000374},
                {0.0, -0.30000000000009797, -0.7468354430380872, 0.021898734177207328}
        };
        System.out.println("Teste 1:" + (testLuDecomposition(matrixToDecompose, matrixToDecomposeResult) ? OK_MESSAGE : NOT_OK_MESSAGE));
        double[][] matrixToDecomposeTwo = {
                {0.0004999999999999449, -0.03, -0.002, -0.001},
                {-0.0005, 0.040000000000000036, -0.004, -0.015},
                {0.0, -0.007, 0.020000000000000018, -0.02},
                {0.0, -0.003, -0.01, 0.050000000000000044}
        };
        double[][] matrixToDecomposeResultTwo = {
                {0.0004999999999999449, -0.03, -0.002, -0.001},
                {-1.0000000000001101, 0.009999999999996734, -0.0060000000000002204, -0.01600000000000011},
                {0.0, -0.7000000000002287, 0.015799999999998492, -0.03120000000000374},
                {0.0, -0.30000000000009797, -0.7468354430380872, 0.021898734177207328}
        };
        System.out.println("Teste 2:" + (testLuDecomposition(matrixToDecomposeTwo, matrixToDecomposeResultTwo) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        double[] sumOfMatrixColumnsExpectedResults = {3, -1, 6, 5};
        double[] sumOfMatrixColumnsExpectedResults1 = {10, 11, 7, 12};

        System.out.println("-- Testing testSumOfMatrixColumns--");
        System.out.println("matrix: {{1, -2, 2, 2}, {0, 4, -2, 1}, {1, -2, 4, 0}, {1, -1, 2, 2}} " + (testSumOfMatrixColumns(matrix11, matrix11.length, sumOfMatrixColumnsExpectedResults) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("matrix: {{3, 2, 1, 4}, {1, 4, 2, 3}, {4, 2, 3, 1}, {2, 3, 1, 4}} " + (testSumOfMatrixColumns(matrix14, matrix14.length, sumOfMatrixColumnsExpectedResults1) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);

        double[][] matrix15 = {{8, 6, 1, 1, 6}, {9, 6, 5, 3, 5}, {5, 4, 2, 2, 9}, {1, 0, 0, 0, 1}, {5, 0, 6, 3, 2}};
        double[][] copyMatrixExpectedResults = {{1, -2, 2, 2}, {0, 4, -2, 1}, {1, -2, 4, 0}, {1, -1, 2, 2}};
        double[][] copyMatrixExpectedResults1 = {{3, 2, 1, 4}, {1, 4, 2, 3}, {4, 2, 3, 1}, {2, 3, 1, 4}};
        double[][] copyMatrixExpectedResults2 = {{1.2, 2.3, 3.4}, {4.5, 5.6, 6.7}, {7.8, 8.9, 10}};
        double[][] copyMatrixExpectedResults3 = {{8, 6, 1, 1, 6}, {9, 6, 5, 3, 5}, {5, 4, 2, 2, 9}, {1, 0, 0, 0, 1}, {5, 0, 6, 3, 2}};

        System.out.println("-- Testing testCopyMatrix--");
        System.out.println("matrix: {{1, -2, 2, 2}, {0, 4, -2, 1}, {1, -2, 4, 0}, {1, -1, 2, 2}} " + (testCopyMatrix(matrix11, matrix11.length, copyMatrixExpectedResults) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("matrix: {{3, 2, 1, 4}, {1, 4, 2, 3}, {4, 2, 3, 1}, {2, 3, 1, 4}} " + (testCopyMatrix(matrix14, matrix14.length, copyMatrixExpectedResults1) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("matrix: {{1.2, 2.3, 3.4}, {4.5, 5.6, 6.7}, {7.8, 8.9, 10}} " + (testCopyMatrix(matrix6, matrix6.length, copyMatrixExpectedResults2) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("matrix: {{8,6,1,1,6},{9,6,5,3,5},{5,4,2,2,9},{1,0,0,0,1},{5,0,6,3,2}} " + (testCopyMatrix(matrix15, matrix15.length, copyMatrixExpectedResults3) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("matrix: {{1, -2, 2, 2}, {0, 4, -2, 1}, {1, -2, 4, 0}, {1, -1, 2, 2}},matrix:{{1, -2, 7, -3}, {0, 1, -4, -2}, {2, -3, 1, 6}, {-1, -2, 3, 1}}" + (testMultiplicationMatrix(matrix11, matrix12, matrixMultiplied1) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println("matrix: {{1, -2, 2, 2}, {0, 4, -2, 1}, {1, -2, 4, 0}, {1, -1, 2, 2}},matrix:{{3, 2, 1, 4}, {1, 4, 2, 3}, {4, 2, 3, 1}, {2, 3, 1, 4}}" + (testMultiplicationMatrix(matrix13, matrix14, matrixMultiplied2) ? OK_MESSAGE : NOT_OK_MESSAGE));
        System.out.println(SEPARATOR);
    }

    // Testa o método para saber se um ano é bissexto
    private static boolean testIsLeapYear(int year, boolean expectedResult) {
        return Covid19Analysis.isLeapYear(year) == expectedResult;
    }

    // Testa o método para saber quantos dias tem um mês num dado ano
    private static boolean testHowManyDaysTheMonthHave(int year, int month, int expectedResult) {
        return Covid19Analysis.howManyDaysTheMonthHave(year, month) == expectedResult;
    }

    //Testa a função para preencher uma string com um determinado número de caracteres
    private static boolean testFillString(char character, int quantity, String expectedResult) {
        return Covid19Analysis.fillString(character, quantity).equals(expectedResult);
    }

    //Testa a função para validar um data
    private static boolean testValidDate(String date, boolean expectedResult) {
        return Covid19Analysis.validDate(date) == expectedResult;
    }

    //Testa a função para formatar corretamente uma data (DD-MM-AAAA para AAAA-MM-DD)
    private static boolean testCorrectlyFormattedDate(String date, String expectedResult) {
        return Covid19Analysis.correctlyFormattedDate(date).equals(expectedResult);
    }

    //Testa a função para contar o número de dias entre duas datas
    private static boolean testCountNumberDaysBetweenTwoDates(String initialDate, String finalDate,
                                                              int expectedResult) throws ParseException {
        return Covid19Analysis.countNumberDaysBetweenTwoDates(initialDate, finalDate) == expectedResult;
    }

    //Testa a função para comparar duas datas
    private static boolean testComparisonBetweenTwoDates(String initialDate, String finalDate, int expectedResult) throws ParseException {
        return Covid19Analysis.comparisonBetweenTwoDates(initialDate, finalDate) == expectedResult;
    }

    //Testa a função para juntar os valores inteiros de uma data em String
    private static boolean testCalendarToString(int year, int month, int day, String expectedResult) {
        return Covid19Analysis.calendarToString(year, month, day).equals(expectedResult);
    }

    //Testa a função para adicionar determinados dias a uma data
    private static boolean testAddDayData(String date, int numberDays, String expectedResult) throws ParseException {
        return Covid19Analysis.addDayData(date, numberDays).equals(expectedResult);
    }

    //Testa a função para calcular a diferença entre dois números
    private static boolean testCalculateDifferenceBetweenTwoNumbers(int firstMeasurement, int secondMesurement,
                                                                    int expectedResult) {
        return Covid19Analysis.calculateDifferenceBetweenTwoNumbers(firstMeasurement, secondMesurement) == expectedResult;
    }

    // Testa o método para descobrir o dia da semana
    private static boolean testIdentifyWeekDay(String date, int expectedResult) throws ParseException {
        return Covid19Analysis.identifyWeekDay(date) == expectedResult;
    }

    // Testa o método que constrói um array com os dados de semanas completas de Segunda-feira a Domingo, a partir de
    //outro array com dados
    private static boolean testShowWeeklyAnalysis(int[][] diaryCovidDataInInterval, int[][] expectedResult) throws ParseException {
        int[][] weeklyAnalysis = Covid19Analysis.showWeeklyAnalysis(diaryCovidDataInInterval);
        return compareArraysInteger(weeklyAnalysis, expectedResult);
    }

    // Testa o método que encontra o índice da primeira ocorrência de um dia da semana, ou seja, de domingo a
    //segunda-feira
    private static boolean testFindIndexOfFirstDayOccurrence(int[][] dateInterval, int neededDay, int expectedResult) throws ParseException {
        return Covid19Analysis.findIndexOfFirstDayOccurrence(dateInterval, neededDay) == expectedResult;
    }

    // Testa o método que encontra o índice da última ocorrência do dia da semana anterior ao dia correspondente ao
    //índice passado por parâmetro
    private static boolean testFindIndexOfLastDayOccurrence(int totalDaysWithData, int intervalDays, int firstDayIndex, int expectedResult) {
        return Covid19Analysis.findIndexOfLastDayOccurrence(totalDaysWithData, intervalDays, firstDayIndex) == expectedResult;
    }

    // Testa o método de leitura de dados
    private static boolean testReadDataFile(String fileName, int expectedResult) throws FileNotFoundException {
        int[][] diaryCovidData = new int[Covid19Analysis.MAXIMUM_RECORDS][Covid19Analysis.TOTAL_FIELDS];
        int totalRecords = Covid19Analysis.readDataFile(diaryCovidData, fileName);
        return totalRecords == expectedResult;
    }

    // Testa o método para saber qual é o possível índice do primeiro dia do próximo mês
    private static boolean testFindNextMonthIndex(int[][] dateInterval, int actualIndex, int expectedResult) {
        return Covid19Analysis.findNextMonthIndex(dateInterval, actualIndex) == expectedResult;
    }

    // Testa o método para saber se o valor de um determinado índice do array é o primeiro dia de um mês e se este mesmo
    //array contem os restantes dias desse mesmo mês (se é um mês completo)
    private static boolean testItsFirstDayAndHaveFullMonth(int[][] dateInterval, int startingDay,
                                                           int arrayLengthWithData, boolean expectedResult) {
        return Covid19Analysis.itsFirstDayAndHaveFullMonth(dateInterval, startingDay, arrayLengthWithData) == expectedResult;
    }

    // Testa o método que encontra o índice da primeira ocorrência de um mês completo num intervalo de datas
    private static boolean testFindIndexOfTheFirstDayOfTheFirstFullMonth(int[][] dateInterval, int lengthWithData, int expectedResult) {
        return Covid19Analysis.findIndexOfTheFirstDayOfTheFirstFullMonth(dateInterval, lengthWithData) == expectedResult;
    }

    // Testa o método que encontra o índice do último dia da última ocorrência de um mês completo num intervalo de datas
    private static boolean testFindIndexOfTheLastDayOfTheLastFullMonth(int[][] dateInterval, int lengthWithData, int expectedResult) {
        return Covid19Analysis.findIndexOfTheLastDayOfTheLastFullMonth(dateInterval, lengthWithData) == expectedResult;
    }

    // Testa o método que retorna o número de meses completos num intervalo de dados
    private static boolean testCountFullMonthsOnAInterval(int[][] dateInterval, int lengthWithData, int expectedResult) {
        return Covid19Analysis.countFullMonthsOnAInterval(dateInterval, lengthWithData) == expectedResult;
    }

    // Testa o método para saber se um dado ficheiro existe
    private static boolean testFileAlreadyExists(String fileName, boolean expectedResult) {
        return Covid19Analysis.fileAlreadyExists(fileName) == expectedResult;
    }

    //Método para comparar arrays do tipo int
    private static boolean compareArraysInteger(int[][] array1, int[][] array2) {
        int day = 0;
        boolean sameValues = false;
        if (array1 != null && array2 != null) {
            sameValues = array1.length == array2.length;

            while (sameValues && day < array1.length) {
                if (!Arrays.equals(array1[day], array2[day])) {
                    sameValues = false;
                }
                day++;
            }
        } else if (array1 == null && array2 == null) {
            sameValues = true;
        }

        return sameValues;
    }

    //Método para comparar arrays
    private static boolean compareArraysDouble(double[][] array1, double[][] array2) {
        int day = 0;
        boolean sameValues = false;
        if (array1 != null && array2 != null) {
            sameValues = array1.length == array2.length;

            while (sameValues && day < array1.length) {
                if (!Arrays.equals(array1[day], array2[day])) {
                    sameValues = false;
                }
                day++;
            }
        } else if (array1 == null && array2 == null) {
            sameValues = true;
        }

        return sameValues;
    }

    //Método para comparar arrays unidimensionais do tipo double
    private static boolean compareArraysDoubleOneDimensional(double[] array1, double[] array2) {
        return Arrays.equals(array1, array2);
    }

    //Método para comparar arrays unidimensionais do tipo int
    private static boolean compareArraysIntegerOneDimensional(int[] array1, int[] array2) {
        return Arrays.equals(array1, array2);
    }

    //Testa a função para criar um array com os valores que constam entres duas datas
    private static boolean testCalculatePosition(int[][] diaryCovidData, int amountOfExistingData, String initialDate,
                                                 String finalDate, boolean accumulatedData, int[][] expectedResult) throws ParseException {
        return compareArraysInteger(Covid19Analysis.calculatePosition(diaryCovidData, amountOfExistingData,
                initialDate, finalDate, accumulatedData), expectedResult);
    }

    //Testa a função para contar o número de dias com dados no array entres duas datas
    private static boolean testCountNumberDaysWithDataBetweenTwoDates(int[][] diaryCovidData, int amountOfExistingData, String initialDate,
                                                                      String finalDate, boolean accumulatedData, int expectedResult) throws ParseException {
        return Covid19Analysis.countNumberDaysWithDataBetweenTwoDates(diaryCovidData, amountOfExistingData,
                initialDate, finalDate, accumulatedData) == expectedResult;
    }

    // Testa o método para construir um array com apenas dados de meses completos
    private static boolean testBuildArrayWithFullMonthlyIntervals(int[][] dateInterval, int[][] expectedResult) throws ParseException {
        int[][] arrayWithFullMonthlyInterval = Covid19Analysis.buildArrayWithFullMonthlyIntervals(dateInterval);
        return compareArraysInteger(arrayWithFullMonthlyInterval, expectedResult);
    }

    // testa o método para somar os valores existentes num array num período de mês ou num período de semana
    private static boolean testCalculateSumOfEveryDayStatistics(int[][] dateInterval, int numberOfLines, String typeSum, int[][] expectedResult) {
        return compareArraysInteger(expectedResult, Covid19Analysis.calculateSumOfEveryDayStatistics(dateInterval, numberOfLines, typeSum));
    }

    //Testa a função para realizar a soma dos valores
    private static boolean testSumValues(int[][] diaryCovidData, int[] expectedResult) {
        return compareArraysIntegerOneDimensional(Covid19Analysis.sumValues(diaryCovidData), expectedResult);
    }

    //Testa a função para realizar a diferença entre os dois periodos
    private static boolean testCalculateDifference(int[][] diaryCovidDataPeriod1, int[][] diaryCovidDataPeriod2,
                                                   int[][] expectedResult) {
        return compareArraysInteger(Covid19Analysis.calculateDifference(diaryCovidDataPeriod1, diaryCovidDataPeriod2), expectedResult);
    }

    //Testa a função para calcular a média
    private static boolean testCalculateAverage(int[][] diaryCovidData, double[] expectedResult) {
        return compareArraysDoubleOneDimensional(Covid19Analysis.calculateAverage(diaryCovidData), expectedResult);
    }

    //Testa a função para calcular o desvio padrão
    private static boolean testCalculateStandardDeviation(int[][] diaryCovidData, double[] expectedResult) {
        return compareArraysDoubleOneDimensional(Covid19Analysis.calculateStandardDeviation(diaryCovidData), expectedResult);
    }

    //Testa a função para criar a matriz de transição através de um ficheiro
    private static boolean testCreateTransitionMatrix(double[][] transitionMatrix, String fileName, boolean expectedResult) throws FileNotFoundException {
        return Covid19Analysis.createTransitionMatrix(transitionMatrix, fileName) == expectedResult;
    }

    // Testa o método que cria a matriz de transição sem o estado absorvente
    private static boolean testCreateTransitionMatrixWithoutAbsorbingState(double[][] matrix, double[][] expectedResult) {
        double[][] result = Covid19Analysis.createTransitionMatrixWithoutAbsorbingState(matrix);
        return compareArraysDouble(result, expectedResult);
    }

    //Testa a função para criar um array com o histórico de dados mais próximo do dia a prever
    private static boolean testLastDayData(int[][] covidTotalDailyData, int countTotalDataRecords, String forecastDate, int[] expectedResult) throws ParseException {
        return compareArraysIntegerOneDimensional(Covid19Analysis.lastDayData(covidTotalDailyData, countTotalDataRecords, forecastDate), expectedResult);
    }

    // Testa a função para calcular a previsão das estatísticas para o dia seguinte através dos dados do dia anterior e
    //da matriz de transição
    private static boolean testCalculateTheForecastForTheNextDay(double[] lastDayData, double[][] transitionMatrix, double[] expectedResult) {
        double[] result = Covid19Analysis.calculateTheForecastForTheNextDay(lastDayData, transitionMatrix);
        return Arrays.equals(result, expectedResult);
    }

    //Testa a função para prever os dados para um determinado dia
    private static boolean testPredictCovidValues(int[] lastDayData, double[][] transitionMatrix, String forecastDate, double[] expectedResult) throws ParseException {
        return compareArraysDoubleOneDimensional(Covid19Analysis.predictCovidValues(lastDayData, transitionMatrix, forecastDate), expectedResult);
    }

    // Testa o método que calcula a quantidade esperada de dias que um individuo demora a morrer
    private static boolean testTheFinalCountdown(double[][] matrix, double[] expectedResult) {
        return Arrays.equals(Covid19Analysis.theFinalCountdown(matrix), expectedResult);
    }

    //Testa o método para subtrair duas matrizes
    private static boolean testSubtractTwoMatrices(double[][] minuedMatrix, double[][] subtrahendMatrix, double[][] expectedResult) {
        return compareArraysDouble(Covid19Analysis.subtractTwoMatrices(minuedMatrix, subtrahendMatrix), expectedResult);
    }

    //Testa o método que cria uma matriz identidade de ordem especificada
    public static boolean testCreateIdentityMatrix(int matrixOrder, double[][] expectedResult) {
        return compareArraysDouble(Covid19Analysis.createIdentityMatrix(matrixOrder), expectedResult);
    }

    //Testa o metodo que multiplica 2 matrizes
    private static boolean testMultiplicationMatrix(double[][] matrix1, double[][] matrix2, double[][] expectedResults) {
        return compareArraysDouble(Covid19Analysis.multiplicationMatrix(matrix1, matrix2), expectedResults);
    }

    // Testa o método que inverte a matriz
    private static boolean testInverseMatrix(double[][] matrixToInverse, double[][] expectedResult) {
        double[][] result = Covid19Analysis.inverseMatrix(matrixToInverse);
        return compareArraysDouble(result, expectedResult);
    }

    // Testa o método para decompor uma matriz em L e U
    private static boolean testLuDecomposition(double[][] matrixToDecompose, double[][] expectedResult) {
        // Por referência
        Covid19Analysis.luDecomposition(matrixToDecompose);
        return compareArraysDouble(matrixToDecompose, expectedResult);
    }

    //Testa o método que soma as colunas de uma matriz
    public static boolean testSumOfMatrixColumns(double[][] matrix, int numberMatrixColumns, double[] expectedResults) {
        return Arrays.equals(Covid19Analysis.sumOfMatrixColumns(matrix, numberMatrixColumns), expectedResults);
    }

    public static boolean testCopyMatrix(double[][] matrix, int size, double[][] expectedResults) {
        return compareArraysDouble(Covid19Analysis.copyMatrix(matrix, size), expectedResults);
    }

}
