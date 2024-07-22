import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Scanner;

public class Covid19Analysis {

    public static final Scanner READ = new Scanner(System.in);
    public static final int MAXIMUM_RECORDS = 1000;
    public static final int TOTAL_FIELDS = 8;
    public static final int FIELDS_WITHOUT_DATE = 5;
    public static final int YEAR_INDEX = 0;
    public static final int MONTH_INDEX = 1;
    public static final int DAY_INDEX = 2;
    public static final int UNINFECTED_INDEX = 3;
    public static final int INFECTED_INDEX = 4;
    public static final int HOSPITALIZED_INDEX = 5;
    public static final int ICU_ADMITTED_INDEX = 6;
    public static final int CASUALTIES_INDEX = 7;
    public static final int START_INDEX_WITHOUT_DATE = TOTAL_FIELDS - FIELDS_WITHOUT_DATE;
    public static final int NUMBER_ARGUMENTS_OPTION1 = 5;
    public static final int NUMBER_ARGUMENTS_OPTION2 = 16;
    public static final int NUMBER_ARGUMENTS_OPTION3 = 20;
    public static final SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd");
    public static final String SEPARATOR = fillString('=', 100);
    public static final String SECOND_SEPARATOR = fillString('-', 100);
    public static final String SEPARATOR_ONE_FIELD = fillString('=', 43);
    public static final String SECOND_SEPARATOR_ONE_FIELD = fillString('-', 43);
    public static final String SEPARATOR_PREDICT_COVID_VALUES = fillString('=', 125);
    public static final String DIARY_TYPE = "Diário";
    public static final String WEEKLY_TYPE = "Semanal";
    public static final String MONTHLY_TYPE = "Mensal";
    public static final String YES_CHOICE = "y";
    public static final String NO_CHOICE = "n";
    public static final int TOTAL_WEEK_DAYS = 7;
    public static final String EXTENSION_TXT = ".txt";
    public static final String EXTENSION_CSV = ".csv";
    public static final double TINY = Math.pow(Math.E, -20);
    public static final double ONE_DECIMAL_PLACE = 10.0;
    public static final double FOUR_DECIMAL_PLACE = 10000.0;
    public static final double[] SUM_OF_TRANSITION_MATRIX_COLUMNS = {1, 1, 1, 1, 1};

    public static void main(String[] args) throws IOException, ParseException {
        int[][] covidAccumulatedDailyData = new int[MAXIMUM_RECORDS][TOTAL_FIELDS];
        int[][] covidTotalDailyData = new int[MAXIMUM_RECORDS][TOTAL_FIELDS];
        int countAccumulatedDataRecords = 0;
        int countTotalDataRecords = 0;

        if (args.length == 0) {
            mainMenu(covidAccumulatedDailyData, covidTotalDailyData, countAccumulatedDataRecords, countTotalDataRecords);
        } else {
            nonInteractiveMode(args, covidAccumulatedDailyData, covidTotalDailyData);
        }
    }

    //Procedimento para realizar a funcionalidade do modo não interativo
    public static void nonInteractiveMode(String[] args, int[][] covidAccumulatedDailyData, int[][] covidTotalDailyData) throws IOException, ParseException {
        if (args.length == NUMBER_ARGUMENTS_OPTION1) {
            nonInteractiveModeOption1(args, covidTotalDailyData);
        } else if (args.length == NUMBER_ARGUMENTS_OPTION2) {
            nonInteractiveModeOption2(args, covidAccumulatedDailyData);
        } else if (args.length == NUMBER_ARGUMENTS_OPTION3) {
            nonInteractiveModeOption3(args, covidAccumulatedDailyData, covidTotalDailyData);
        } else {
            System.out.println("Erro! Comando inválido! Exemplo: java -jar nome programa.jar -r X -di DD-MM-AAAA -df DD-MM-AAAA " +
                    "-di1 DD-MMAAAA -df1 DD-MM-AAAA -di2 DD-MM-AAAA -df2 DD-MM-AAAA -T DD-MM-AAAA " +
                    "registoNumeroTotalCovid19.csv registoNumerosAcumuladosCovid19.csv matrizTransicao.txt nomeficheirosaida.txt");
        }
    }

    //Procedimento para realizar a funcionalidade do modo não interativo relativo à opção 1
    public static void nonInteractiveModeOption1(String[] args, int[][] covidTotalDailyData) throws IOException, ParseException {
        String parameterT = args[0], forecastDate = correctlyFormattedDate(args[1]), fileTotalData = args[2], fileTransitionMatrix = args[3], writeFile = args[4];

        if (validArgumentsOption1(parameterT, forecastDate, fileTotalData, fileTransitionMatrix)) {
            int countTotalDataRecords = readDataFile(covidTotalDailyData, fileTotalData);

            //Previsão dos dados para um determinado dia
            double[][] transitionMatrix = new double[FIELDS_WITHOUT_DATE][FIELDS_WITHOUT_DATE];

            if (createTransitionMatrix(transitionMatrix, fileTransitionMatrix)) {
                int[] lastDayData = lastDayData(covidTotalDailyData, countTotalDataRecords, forecastDate);

                if (lastDayData != null) {
                    double[] forecastForTheNextDay = predictCovidValues(lastDayData, transitionMatrix, forecastDate);
                    printPredictCovidValues(forecastForTheNextDay, forecastDate);
                    savePredictCovidValuesInCsv(forecastForTheNextDay, forecastDate, writeFile, ONE_DECIMAL_PLACE);
                } else {
                    System.out.println("Não é possível fazer uma previsão de uma data anterior ao lote de dados disponíveis!");
                }

                double[] amountOfDaysTransitionBetweenStatus = theFinalCountdown(transitionMatrix);
                printExpectedAmountOfDaysTransitionBetweenStatus(amountOfDaysTransitionBetweenStatus);
                saveExpectedAmountOfDaysTransitionBetweenStatus(amountOfDaysTransitionBetweenStatus, writeFile, ONE_DECIMAL_PLACE);

            } else {
                System.out.println("A matriz de transição encontra-se errada!");
            }

        } else {
            System.out.println("Erro! Parâmetros inválidos! Exemplo: java -jar nomeprograma.jar -T DD-MM-AAAA " +
                    "registoNumerosTotalCovid19.csv matrizTransicao.csv nomeficheirosaida.txt");
        }

    }

    //Procedimento para realizar a funcionalidade do modo não interativo relativo à opção 2
    public static void nonInteractiveModeOption2(String[] args, int[][] covidAccumulatedDailyData) throws IOException, ParseException {
        String parameterR = args[0], temporalResolution = args[1], fileAccumulatedData = args[14], writeFile = args[15], temporalResolutionType = "";
        String[] dates = new String[12];

        for (int i = 0; i < 12; i++) {
            dates[i] = correctlyFormattedDate(args[i + 2]);
        }

        if (validArgumentsOption2(parameterR, temporalResolution, dates, fileAccumulatedData)) {
            //Dados Acumulados
            int countAccumulatedDataRecords = readDataFile(covidAccumulatedDailyData, fileAccumulatedData);
            int[][] diaryCovidDataInInterval = calculatePosition(covidAccumulatedDailyData, countAccumulatedDataRecords, correctlyFormattedDate(dates[1]), correctlyFormattedDate(dates[3]), true);

            //Análise Diária/Semanal/Mensal para os dados acumulados
            switch (temporalResolution) {
                case "0":
                    temporalResolutionType = DIARY_TYPE;
                    break;
                case "1":
                    temporalResolutionType = WEEKLY_TYPE;
                    break;
                case "2":
                    temporalResolutionType = MONTHLY_TYPE;
                    break;
            }

            //Imprimir dados da análise a diferentes resoluções temporais no ecrã
            diaryCovidDataInInterval = temporalResolutionOptions(diaryCovidDataInInterval, temporalResolutionType, TOTAL_FIELDS);
            //Guardar dados da análise a diferentes resoluções temporais num ficheiro
            if (diaryCovidDataInInterval != null && diaryCovidDataInInterval.length > 0) {
                saveCovidDataInCsv(diaryCovidDataInInterval, temporalResolutionType, TOTAL_FIELDS, writeFile);
            }

            //Array com os 2 intervalos de datas para a análise comparativa
            String[] dateRange = {correctlyFormattedDate(dates[5]), correctlyFormattedDate(dates[7]), correctlyFormattedDate(dates[9]), correctlyFormattedDate(dates[11])};
            //Análise comparativa para os dados acumulados
            int[][] diaryCovidAccumulatedDataPeriod1 = calculatePosition(covidAccumulatedDailyData, countAccumulatedDataRecords, dateRange[0], dateRange[1], true);
            int[][] diaryCovidAccumulatedDataPeriod2 = calculatePosition(covidAccumulatedDailyData, countAccumulatedDataRecords, dateRange[2], dateRange[3], true);

            if (comparativeAnalysisOptions(diaryCovidAccumulatedDataPeriod1, diaryCovidAccumulatedDataPeriod2, dateRange, TOTAL_FIELDS)) {
                saveComparativeAnalysisInCsv(diaryCovidAccumulatedDataPeriod1, diaryCovidAccumulatedDataPeriod2, TOTAL_FIELDS, writeFile);
            }
        } else {
            System.out.println("Erro! Parâmetros inválidos! Exemplo: java -jar nomeprograma.jar -r X -di DD-MM-AAAA " +
                    "-df DD-MMAAAA -di1 DD-MM-AAAA -df1 DD-MM-AAAA -di2 DD-MM-AAAA -df2 DD-MM-AAAA " +
                    "registoNumerosAcumuladosCovid19.csv nomeficheirosaida.txt");
        }
    }

    //Procedimento para realizar a funcionalidade do modo não interativo relativo à opção 3
    public static void nonInteractiveModeOption3(String[] args, int[][] covidAccumulatedDailyData, int[][] covidTotalDailyData) throws IOException, ParseException {
        String parameterR = args[0], temporalResolution = args[1], parameterT = args[14], forecastDate = correctlyFormattedDate(args[15]), fileTotalData = args[16],
                fileAccumulatedData = args[17], fileTransitionMatrix = args[18], writeFile = args[19], temporalResolutionType = "";
        String[] dates = new String[12];

        for (int i = 0; i < 12; i++) {
            dates[i] = correctlyFormattedDate(args[i + 2]);
        }

        if (validArgumentsOption1(parameterT, forecastDate, fileTotalData, fileTransitionMatrix)
                && validArgumentsOption2(parameterR, temporalResolution, dates, fileAccumulatedData)) {

            //Dados Acumulados
            int countAccumulatedDataRecords = readDataFile(covidAccumulatedDailyData, fileAccumulatedData);
            int[][] diaryCovidAccumulatedDataInInterval = calculatePosition(covidAccumulatedDailyData, countAccumulatedDataRecords, correctlyFormattedDate(dates[1]), correctlyFormattedDate(dates[3]), true);

            //Dados Totais
            int countTotalDataRecords = readDataFile(covidTotalDailyData, fileTotalData);
            int[][] diaryCovidTotalDataInInterval = calculatePosition(covidTotalDailyData, countTotalDataRecords, correctlyFormattedDate(dates[1]), correctlyFormattedDate(dates[3]), false);

            //Análise Diária/Semanal/Mensal para os dados acumulados
            switch (temporalResolution) {
                case "0":
                    temporalResolutionType = DIARY_TYPE;
                    break;
                case "1":
                    temporalResolutionType = WEEKLY_TYPE;
                    break;
                case "2":
                    temporalResolutionType = MONTHLY_TYPE;
                    break;
            }

            //Imprimir dados da análise a diferentes resoluções temporais no ecrã
            diaryCovidAccumulatedDataInInterval = temporalResolutionOptions(diaryCovidAccumulatedDataInInterval, temporalResolutionType, TOTAL_FIELDS);
            diaryCovidTotalDataInInterval = temporalResolutionOptions(diaryCovidTotalDataInInterval, temporalResolutionType, TOTAL_FIELDS);

            //Guardar dados da análise a diferentes resoluções temporais num ficheiro
            if (diaryCovidAccumulatedDataInInterval != null && diaryCovidAccumulatedDataInInterval.length > 0) {
                saveCovidDataInCsv(diaryCovidAccumulatedDataInInterval, temporalResolutionType, TOTAL_FIELDS, writeFile);
            }
            if (diaryCovidTotalDataInInterval != null && diaryCovidTotalDataInInterval.length > 0) {
                saveCovidDataInCsv(diaryCovidTotalDataInInterval, temporalResolutionType, TOTAL_FIELDS, writeFile);
            }

            //Array com os 2 intervalos de datas para a análise comparativa
            String[] dateRange = {correctlyFormattedDate(dates[5]), correctlyFormattedDate(dates[7]), correctlyFormattedDate(dates[9]), correctlyFormattedDate(dates[11])};

            //Análise comparativa para os dados acumulados
            int[][] diaryCovidAccumulatedDataPeriod1 = calculatePosition(covidAccumulatedDailyData, countAccumulatedDataRecords, dateRange[0], dateRange[1], true);
            int[][] diaryCovidAccumulatedDataPeriod2 = calculatePosition(covidAccumulatedDailyData, countAccumulatedDataRecords, dateRange[2], dateRange[3], true);

            if (comparativeAnalysisOptions(diaryCovidAccumulatedDataPeriod1, diaryCovidAccumulatedDataPeriod2, dateRange, TOTAL_FIELDS)) {
                saveComparativeAnalysisInCsv(diaryCovidAccumulatedDataPeriod1, diaryCovidAccumulatedDataPeriod2, TOTAL_FIELDS, writeFile);
            }

            //Análise comparativa para os dados totais
            int[][] diaryCovidTotalDataPeriod1 = calculatePosition(covidTotalDailyData, countTotalDataRecords, dateRange[0], dateRange[1], false);
            int[][] diaryCovidTotalDataPeriod2 = calculatePosition(covidTotalDailyData, countTotalDataRecords, dateRange[2], dateRange[3], false);

            if (comparativeAnalysisOptions(diaryCovidTotalDataPeriod1, diaryCovidTotalDataPeriod2, dateRange, TOTAL_FIELDS)) {
                saveComparativeAnalysisInCsv(diaryCovidTotalDataPeriod1, diaryCovidTotalDataPeriod2, TOTAL_FIELDS, writeFile);
            }

            //Previsão dos dados para um determinado dia
            double[][] transitionMatrix = new double[FIELDS_WITHOUT_DATE][FIELDS_WITHOUT_DATE];
            if (createTransitionMatrix(transitionMatrix, fileTransitionMatrix)) {
                int[] lastDayData = lastDayData(covidTotalDailyData, countTotalDataRecords, forecastDate);

                if (lastDayData != null) {
                    double[] forecastForTheNextDay = predictCovidValues(lastDayData, transitionMatrix, forecastDate);
                    printPredictCovidValues(forecastForTheNextDay, forecastDate);
                    savePredictCovidValuesInCsv(forecastForTheNextDay, forecastDate, writeFile, ONE_DECIMAL_PLACE);
                } else {
                    System.out.println("Não é possível fazer uma previsão de uma data anterior ao lote de dados disponíveis!");
                }

                double[] amountOfDaysTransitionBetweenStatus = theFinalCountdown(transitionMatrix);
                printExpectedAmountOfDaysTransitionBetweenStatus(amountOfDaysTransitionBetweenStatus);
                saveExpectedAmountOfDaysTransitionBetweenStatus(amountOfDaysTransitionBetweenStatus, writeFile, ONE_DECIMAL_PLACE);

            } else {
                System.out.println("A matriz de transição encontra-se errada!");
            }

        } else {
            System.out.println("Erro! Parâmetros inválidos! Exemplo: java -jar nome programa.jar -r X -di DD-MM-AAAA -df DD-MM-AAAA " +
                    "-di1 DD-MMAAAA -df1 DD-MM-AAAA -di2 DD-MM-AAAA -df2 DD-MM-AAAA -T DD-MM-AAAA " +
                    "registoNumeroTotalCovid19.csv registoNumerosAcumuladosCovid19.csv matrizTransicao.txt nomeficheirosaida.txt");
        }
    }

    //Função para validar os argumentos na opção 1 do modo não interativo
    public static boolean validArgumentsOption1(String parameterT, String forecastDate, String readFileTotalData, String transitionMatrix) {
        boolean validArgumentsOption1 = false;

        if (parameterT.equals("-T")) {
            if (!validDate(forecastDate)) {
                System.out.println("A data encontra-se inválida!");

            } else if (!fileAlreadyExists(readFileTotalData)) {
                System.out.println("O ficheiro que deve conter os registos totais não existe!");

            } else if (!fileAlreadyExists(transitionMatrix)) {
                System.out.println("O ficheiro que deve conter a matriz de transição não existe!");

            } else {
                validArgumentsOption1 = true;
            }
        }

        return validArgumentsOption1;
    }

    //Função para validar os argumentos na opção 2 do modo não interativo
    public static boolean validArgumentsOption2(String parameterR, String temporalResolution, String[] dataRange, String readFileAccumulatedData) throws ParseException {
        boolean validArgumentsOption2 = false;

        if (parameterR.equals("-r") && dataRange[0].equals("-di") && dataRange[2].equals("-df") && dataRange[4].equals("-di1")
                && dataRange[6].equals("-df1") && dataRange[8].equals("-di2") && dataRange[10].equals("-df2")) {

            if (!(temporalResolution.equals("0") || temporalResolution.equals("1") || temporalResolution.equals("2"))) {
                System.out.println("A resolução temporal encontra-se inválida!");

            } else if (!(validDate(dataRange[1]) && validDate(dataRange[3])
                    && validDate(dataRange[5]) && validDate(dataRange[7])
                    && validDate(dataRange[9]) && validDate(dataRange[11]))) {
                System.out.println("A data encontra-se inválida!");

            } else if ((comparisonBetweenTwoDates(dataRange[1], dataRange[3]) < 0)
                    || (comparisonBetweenTwoDates(dataRange[5], dataRange[7]) < 0)
                    || (comparisonBetweenTwoDates(dataRange[9], dataRange[11]) < 0)) {
                System.out.println("Intervalo de datas inválido!");

            } else if (!fileAlreadyExists(readFileAccumulatedData)) {
                System.out.println("O ficheiro que deve conter os registos acumulados não existe!");

            } else {
                validArgumentsOption2 = true;
            }
        }
        return validArgumentsOption2;
    }

    // Fazer a leitura dos dados do ficheiro
    public static int readDataFile(int[][] diaryCovidData, String fileName) throws FileNotFoundException {

        int amountOfRecordedData = 0;

        if (fileAlreadyExists(fileName)) {
            File file = new File(fileName);
            Scanner scannerFile = new Scanner(file);
            // Ler o cabeçalho
            String header = scannerFile.nextLine();

            while (scannerFile.hasNextLine()) {

                String[] lineValues = scannerFile.nextLine().split(",");
                String[] dateValuesInString = correctlyFormattedDate(lineValues[0]).split("-");

                // Espaços reservados para a data
                diaryCovidData[amountOfRecordedData][YEAR_INDEX] = Integer.parseInt(dateValuesInString[0]); // ano
                diaryCovidData[amountOfRecordedData][MONTH_INDEX] = Integer.parseInt(dateValuesInString[1]); // mes
                diaryCovidData[amountOfRecordedData][DAY_INDEX] = Integer.parseInt(dateValuesInString[2]); // dia
                // Espaços reservados para o resto da informação
                diaryCovidData[amountOfRecordedData][UNINFECTED_INDEX] = Integer.parseInt(lineValues[1]); //diario_nao_infetado
                diaryCovidData[amountOfRecordedData][INFECTED_INDEX] = Integer.parseInt(lineValues[2]); //acumulado_infetado
                diaryCovidData[amountOfRecordedData][HOSPITALIZED_INDEX] = Integer.parseInt(lineValues[3]); //acumulado_hospitalizado
                diaryCovidData[amountOfRecordedData][ICU_ADMITTED_INDEX] = Integer.parseInt(lineValues[4]); //acumulado_internadoUCI
                diaryCovidData[amountOfRecordedData][CASUALTIES_INDEX] = Integer.parseInt(lineValues[5]); //acumulado_mortes

                amountOfRecordedData++;
            }

            scannerFile.close();
        }

        return amountOfRecordedData;

    }

    // Preparar a leitura de dados de um ficheiro com uma dada extensão
    public static int readFile(int[][] diaryCovidData, String fileName, String extension) throws FileNotFoundException {

        int amountOfRecordedData = 0;

        if (fileName.length() > extension.length()) {
            String fileExtension = fileName.substring(fileName.length() - extension.length());
            if (fileAlreadyExists(fileName)) {
                if (extension.equalsIgnoreCase(fileExtension)) {

                    amountOfRecordedData = readDataFile(diaryCovidData, fileName);
                    if (amountOfRecordedData == 0) {
                        System.out.println("Não existem dados no ficheiro!");
                    } else {
                        System.out.println("Ficheiro importado com sucesso!");
                    }

                } else {
                    System.out.println("O ficheiro indicado não contém a extensão pretendida (" + extension + ")!");
                }
            } else {
                System.out.println("O ficheiro indicado não existe!");
            }
        } else {
            System.out.println("O nome do ficheiro é inválido!");
        }

        return amountOfRecordedData;
    }

    // Texto a ser apresentado no menu principal
    public static void textMainMenu() {
        String separator = "|" + fillString('=', 50) + "|";
        System.out.println();
        System.out.println(separator);
        System.out.println("|                  Menu Principal                  |");
        System.out.println(separator);
        System.out.println("| 1 - Carregar ficheiro                            |");
        System.out.println("| 2 - Análise de diferentes resoluções temporais   |");
        System.out.println("| 3 - Análise comparativa                          |");
        System.out.println("| 4 - Previsão da evolução da pandemia             |");
        System.out.println("| 5 - Termina o programa                           |");
        System.out.println(separator);
        System.out.println("Introduza a opção que pretende");
    }

    // Texto a ser apresentado na escolha de carregamento do ficheiro
    public static void textFileChoiceOptionsMenu() {
        String separator = "|" + fillString('=', 50) + "|";
        System.out.println();
        System.out.println(separator);
        System.out.println("|              Importação de ficheiros             |");
        System.out.println(separator);
        System.out.println("| 1 - Carregar ficheiro com os dados acumulados    |");
        System.out.println("| 2 - Carregar ficheiro com os dados totais        |");
        System.out.println("| 3 - Carregar ficheiro com a matriz de transição  |");
        System.out.println("| 4 - Voltar ao Menu Principal                     |");
        System.out.println(separator);
        System.out.println("Introduza a opção que pretende");
    }

    // Texto a ser apresentado na escolha de visualização dos dados
    public static void textViewTypeMenu() {
        String separator = "|" + fillString('=', 50) + "|";
        System.out.println();
        System.out.println(separator);
        System.out.println("|               Tipo de Visualização               |");
        System.out.println(separator);
        System.out.println("| 1 - Visualizar a análise dos novos casos Covid   |");
        System.out.println("| 2 - Visualizar a análise dos dados totais Covid  |");
        System.out.println("| 3 - Voltar ao Menu Principal                     |");
        System.out.println(separator);
        System.out.println("Introduza a opção que pretende");
    }

    // Texto a ser apresentado no menu secundário
    public static void textTemporalAnalysisMenu() {
        String separator = "|" + fillString('=', 30) + "|";
        System.out.println();
        System.out.println(separator);
        System.out.println("|       Análise temporal       |");
        System.out.println(separator);
        System.out.println("| 1 - Análise Diária           |");
        System.out.println("| 2 - Analise Semanal          |");
        System.out.println("| 3 - Análise Mensal           |");
        System.out.println("| 4 - Voltar ao Menu Anterior  |");
        System.out.println(separator);
        System.out.println("Introduza a opção que pretende");
    }

    // Texto a ser apresentado no menu terceiro
    public static void textPrevisionMenu() {
        String separator = "|" + fillString('=', 80) + "|";
        System.out.println();
        System.out.println(separator);
        System.out.println("|                               Previsão de dados                                |");
        System.out.println(separator);
        System.out.println("| 1 - Previsão da quantidade esperada de dias que um indivíduo demora a morrer   |");
        System.out.println("| 2 - Previsão das estatisticas de covid num determinado momento                 |");
        System.out.println("| 3 - Voltar ao Menu Principal                                                   |");
        System.out.println(separator);
        System.out.println("Introduza a opção que pretende");
    }

    // Texto a ser apresentado na escolha do tipo de dados a visualizar
    public static void textChoiceOfDataTypeMenu() {
        String separator = "|" + fillString('=', 50) + "|";
        System.out.println();
        System.out.println(separator);
        System.out.println("|                  Tipo de Dados                   |");
        System.out.println(separator);
        System.out.println("| 1 - Número de infetados                          |");
        System.out.println("| 2 - Número de hospitalizados                     |");
        System.out.println("| 3 - Número de internados UCI                     |");
        System.out.println("| 4 - Número de mortos                             |");
        System.out.println("| 5 - Todos                                        |");
        System.out.println(separator);
        System.out.println("Introduza a opção que pretende");
    }

    public static int choiceOfDataTypeToField(String choiceOfDataType) {
        switch (choiceOfDataType) {
            case "1":
                return INFECTED_INDEX;
            case "2":
                return HOSPITALIZED_INDEX;
            case "3":
                return ICU_ADMITTED_INDEX;
            case "4":
                return CASUALTIES_INDEX;
            default:
                return TOTAL_FIELDS;
        }
    }

    // Menu principal do programa
    public static void mainMenu(int[][] covidAccumulatedDailyData, int[][] covidTotalDailyData,
                                int countAccumulatedDataRecords, int countTotalDataRecords) throws IOException, ParseException {

        String command;
        double[][] transitionMatrix = null;

        do {
            textMainMenu();
            command = READ.nextLine().trim();
            String secondaryOption;

            switch (command) {
                case "1":
                    String file;
                    do {
                        textFileChoiceOptionsMenu();
                        secondaryOption = READ.nextLine().trim();

                        if (secondaryOption.equals("1") || secondaryOption.equals("2") || secondaryOption.equals("3")) {
                            System.out.println("Introduza o nome do ficheiro:");
                            file = READ.nextLine();

                            switch (secondaryOption) {
                                case "1":
                                    countAccumulatedDataRecords = readFile(covidAccumulatedDailyData, file, EXTENSION_CSV);
                                    break;
                                case "2":
                                    countTotalDataRecords = readFile(covidTotalDailyData, file, EXTENSION_CSV);
                                    break;
                                case "3":
                                    transitionMatrix = new double[FIELDS_WITHOUT_DATE][FIELDS_WITHOUT_DATE];
                                    if (!readFileTransitionMatrix(transitionMatrix, file, EXTENSION_TXT)) {
                                        transitionMatrix = null;
                                    }
                                    break;
                            }
                        } else if (secondaryOption.equals("4")) {
                            System.out.println("A voltar para o Menu Principal...");
                        } else {
                            System.out.println("Por favor, introduza uma opção válida!");
                        }

                    } while (!secondaryOption.equals("4"));
                    break;
                case "2":
                    do {
                        textViewTypeMenu();
                        secondaryOption = READ.nextLine().trim();

                        switch (secondaryOption) {
                            case "1":
                                if (countAccumulatedDataRecords == 0) {
                                    System.out.println("Para aceder a esta funcionalidade o ficheiro com os dados acumulados deve estar carregado.");
                                } else {
                                    temporalAnalysisMenu(covidAccumulatedDailyData, countAccumulatedDataRecords, true);
                                }
                                break;
                            case "2":
                                if (countTotalDataRecords == 0) {
                                    System.out.println("Para aceder a esta funcionalidade o ficheiro com os dados totais deve estar carregado.");
                                } else {
                                    temporalAnalysisMenu(covidTotalDailyData, countTotalDataRecords, false);
                                }
                                break;
                            case "3":
                                System.out.println("A voltar para o Menu Principal...");
                                break;
                            default:
                                System.out.println("Por favor, introduza uma opção válida!");
                                break;
                        }
                    } while (!secondaryOption.equals("3"));
                    break;
                case "3":
                    do {
                        textViewTypeMenu();
                        secondaryOption = READ.nextLine().trim();

                        boolean processData = secondaryOption.equals("1") || secondaryOption.equals("2");

                        if (secondaryOption.equals("1") && countAccumulatedDataRecords == 0) {
                            System.out.println("Para aceder a esta funcionalidade o ficheiro com os dados acumulados deve estar carregado.");
                        } else if (secondaryOption.equals("2") && countTotalDataRecords == 0) {
                            System.out.println("Para aceder a esta funcionalidade o ficheiro com os dados totais deve estar carregado.");
                        } else if (processData) {
                            String[] dateRange = readDateRange(2);
                            int[][] diaryCovidDataPeriod1 = null, diaryCovidDataPeriod2 = null;

                            textChoiceOfDataTypeMenu();
                            String choiceOfDataType = READ.nextLine().trim();

                            while (!choiceOfDataType.equals("1") && !choiceOfDataType.equals("2") && !choiceOfDataType.equals("3") && !choiceOfDataType.equals("4") && !choiceOfDataType.equals("5")) {
                                System.out.println("Por favor, introduza uma opção válida!");
                                choiceOfDataType = READ.nextLine().trim();
                            }

                            switch (secondaryOption) {
                                case "1":
                                    diaryCovidDataPeriod1 = calculatePosition(covidAccumulatedDailyData, countAccumulatedDataRecords, dateRange[0], dateRange[1], true);
                                    diaryCovidDataPeriod2 = calculatePosition(covidAccumulatedDailyData, countAccumulatedDataRecords, dateRange[2], dateRange[3], true);
                                    break;
                                case "2":
                                    diaryCovidDataPeriod1 = calculatePosition(covidTotalDailyData, countTotalDataRecords, dateRange[0], dateRange[1], false);
                                    diaryCovidDataPeriod2 = calculatePosition(covidTotalDailyData, countTotalDataRecords, dateRange[2], dateRange[3], false);
                                    break;
                            }

                            if (comparativeAnalysisOptions(diaryCovidDataPeriod1, diaryCovidDataPeriod2, dateRange, choiceOfDataTypeToField(choiceOfDataType))) {
                                if (wantSaveFile()) {
                                    System.out.println("Escreva o nome que deseja dar ao ficheiro a criar: ");
                                    String fileName = READ.nextLine();
                                    saveComparativeAnalysisInCsv(diaryCovidDataPeriod1, diaryCovidDataPeriod2, choiceOfDataTypeToField(choiceOfDataType), fileName);
                                    System.out.println("Ficheiro guardado.");
                                } else {
                                    System.out.println("Os dados não foram guardados.");
                                }
                            }
                        } else if (secondaryOption.equals("3")) {
                            System.out.println("A voltar para o Menu Principal...");
                        } else {
                            System.out.println("Por favor, introduza uma opção válida!");
                        }
                    } while (!secondaryOption.equals("3"));
                    break;
                case "4":
                    if (transitionMatrix != null) {
                        previsionMenu(transitionMatrix, covidTotalDailyData, countTotalDataRecords);
                    } else {
                        System.out.println("Para aceder a esta funcionalidade o ficheiro com a matriz de transição deve estar carregado.");
                    }
                    break;
                case "5":
                    System.out.println("Obrigado por utilizar a nossa aplicação! Até uma próxima!");
                    break;
                default:
                    System.out.println("Por favor, introduza uma opção válida!");
                    break;
            }

        } while (!command.equals("5"));
    }

    // Menu secundário
    public static void temporalAnalysisMenu(int[][] diaryCovidData, int amountOfExistingData, boolean accumulatedData) throws
            IOException, ParseException {

        String[] dates;
        int[][] diaryCovidDataInInterval = null;

        String command;
        String choiceOfDataType = "", temporalResolutionType = "";

        do {
            textTemporalAnalysisMenu();
            command = READ.nextLine().trim();

            // Se não for para sair do menu, será necessário processar dados
            boolean processData = command.equals("1") || command.equals("2") || command.equals("3");

            if (processData) {
                dates = readDateRange(1);

                textChoiceOfDataTypeMenu();
                choiceOfDataType = READ.nextLine().trim();

                while (!choiceOfDataType.equals("1") && !choiceOfDataType.equals("2") && !choiceOfDataType.equals("3") && !choiceOfDataType.equals("4") && !choiceOfDataType.equals("5")) {
                    System.out.println("Por favor, introduza uma opção válida!");
                    choiceOfDataType = READ.nextLine().trim();
                }

                diaryCovidDataInInterval = calculatePosition(diaryCovidData, amountOfExistingData, dates[0], dates[1], accumulatedData);
            }

            switch (command) {
                case "1":
                    temporalResolutionType = DIARY_TYPE;
                    diaryCovidDataInInterval = temporalResolutionOptions(diaryCovidDataInInterval, temporalResolutionType, choiceOfDataTypeToField(choiceOfDataType));
                    break;
                case "2":
                    temporalResolutionType = WEEKLY_TYPE;
                    diaryCovidDataInInterval = temporalResolutionOptions(diaryCovidDataInInterval, temporalResolutionType, choiceOfDataTypeToField(choiceOfDataType));
                    break;
                case "3":
                    temporalResolutionType = MONTHLY_TYPE;
                    diaryCovidDataInInterval = temporalResolutionOptions(diaryCovidDataInInterval, temporalResolutionType, choiceOfDataTypeToField(choiceOfDataType));
                    break;
                case "4":
                    System.out.println("A voltar para o Menu Anterior...");
                    break;
                default:
                    System.out.println("Introduza uma opção válida");
                    break;
            }

            if (processData && diaryCovidDataInInterval != null && diaryCovidDataInInterval.length > 0) {
                if (saveCalculatedDataIntoFile(diaryCovidDataInInterval, temporalResolutionType, choiceOfDataTypeToField(choiceOfDataType))) {
                    System.out.println("Ficheiro guardado.");
                } else {
                    System.out.println("Os dados não foram guardados.");
                }
            }
        } while (!command.equals("4"));
    }

    //Função com as opções relativas às resoluções temporais
    public static int[][] temporalResolutionOptions(int[][] diaryCovidDataInInterval, String temporalResolutionType, int indexDataType) throws ParseException {
        switch (temporalResolutionType) {
            case DIARY_TYPE:
                if (diaryCovidDataInInterval.length == 0) {
                    System.out.println("Não existem dados para o intervalo de dados indicado!");
                } else {
                    printDataTemporalResolution(diaryCovidDataInInterval, temporalResolutionType, indexDataType);
                }
                break;

            case WEEKLY_TYPE:
                if (diaryCovidDataInInterval.length < TOTAL_WEEK_DAYS) {
                    System.out.println("Não existem dados de pelo menos uma semana completa!");
                    diaryCovidDataInInterval = null;
                } else {
                    int[][] dataToPrint = showWeeklyAnalysis(diaryCovidDataInInterval);
                    diaryCovidDataInInterval = calculateSumOfEveryDayStatistics(dataToPrint, dataToPrint.length / TOTAL_WEEK_DAYS, temporalResolutionType);
                    printDataTemporalResolution(diaryCovidDataInInterval, temporalResolutionType, indexDataType);
                }
                break;

            case MONTHLY_TYPE:
                int totalMonthsToSave = countFullMonthsOnAInterval(diaryCovidDataInInterval, diaryCovidDataInInterval.length);
                if (totalMonthsToSave < 1) {
                    System.out.println("Não existem dados de pelo menos um mês completo!");
                    diaryCovidDataInInterval = null;
                } else {
                    int[][] dataToPrint = buildArrayWithFullMonthlyIntervals(diaryCovidDataInInterval);
                    diaryCovidDataInInterval = calculateSumOfEveryDayStatistics(dataToPrint, totalMonthsToSave, temporalResolutionType);
                    printDataTemporalResolution(diaryCovidDataInInterval, temporalResolutionType, indexDataType);
                }
        }

        System.out.println();

        return diaryCovidDataInInterval;
    }

    // Menu onde é possível verificar previsões de estatísticas
    public static void previsionMenu(double[][] transitionMatrix, int[][] covidTotalDailyData, int countTotalDataRecords) throws IOException, ParseException {

        String command;
        do {
            textPrevisionMenu();
            command = READ.nextLine().trim();

            switch (command) {
                case "1":
                    double[] amountOfDaysTransitionBetweenStatus = theFinalCountdown(transitionMatrix);
                    printExpectedAmountOfDaysTransitionBetweenStatus(amountOfDaysTransitionBetweenStatus);
                    if (wantSaveFile()) {
                        System.out.println("Escreva o nome que deseja dar ao ficheiro a criar: ");
                        String fileName = READ.nextLine();
                        saveExpectedAmountOfDaysTransitionBetweenStatus(amountOfDaysTransitionBetweenStatus, fileName, ONE_DECIMAL_PLACE);
                        System.out.println("Ficheiro guardado com sucesso!");
                    } else {
                        System.out.println("Os dados não foram guardados.");
                    }
                    break;
                case "2":
                    if (countTotalDataRecords == 0) {
                        System.out.println("Para aceder a esta funcionalidade o ficheiro com os dados totais deve estar carregado.");
                    } else {
                        System.out.println("Insira a data que pretende fazer uma previsão (Formato AAAA-MM-DD ou DD-MM-AAAA): ");
                        String date = readDate();

                        int[] lastDayData = lastDayData(covidTotalDailyData, countTotalDataRecords, date);
                        if (lastDayData != null) {
                            double[] forecastForTheNextDay = predictCovidValues(lastDayData, transitionMatrix, date);
                            printPredictCovidValues(forecastForTheNextDay, date);

                            if (wantSaveFile()) {
                                System.out.println("Escreva o nome que deseja dar ao ficheiro a criar: ");
                                String fileName = READ.nextLine();
                                savePredictCovidValuesInCsv(forecastForTheNextDay, date, fileName, ONE_DECIMAL_PLACE);
                                System.out.println("Ficheiro guardado.");
                            } else {
                                System.out.println("Os dados não foram guardados.");
                            }
                        } else {
                            System.out.println("Não é possível fazer uma previsão de uma data anterior ao lote de dados disponíveis");
                        }
                    }
                    break;
                case "3":
                    System.out.println("A voltar para o Menu Principal...");
                    break;
            }
        } while (!command.equals("3"));
    }

    public static void saveExpectedAmountOfDaysTransitionBetweenStatus(double[] matrixWithData, String fileName, double decimalPlaces) throws IOException {

        PrintWriter fWriter = new PrintWriter(new FileWriter(fileName, true));

        fWriter.printf("numeroDias,naoInfetados,infetados,hospitalizados,internadosUCI\n");
        fWriter.printf("%s", "numeroDias");

        for (int data = 0; data < matrixWithData.length; data++) {
            fWriter.printf(",%s", String.valueOf(Math.round(matrixWithData[data] * decimalPlaces) / decimalPlaces).replace(",", "."));
        }

        fWriter.printf("%n");
        fWriter.close();
    }

    public static void printExpectedAmountOfDaysTransitionBetweenStatus(double[] matrixWithData) {
        System.out.println(SEPARATOR);
        System.out.printf("%20s %19s %19s %19s %19s %n", "", "NÃO INFETADOS", "INFETADOS", "HOSPITALIZADOS", "INTERNADOS UCI");
        System.out.println(SEPARATOR);
        System.out.printf("%20s", "NÚMERO DE DIAS");
        for (int data = 0; data < matrixWithData.length; data++) {
            System.out.printf(" %19.1f", matrixWithData[data]);
        }
        System.out.printf("%n");
        System.out.println(SEPARATOR);
    }

    // Metodo para selecionar se é para guardar o ficheiro ou se é para ir para o menu anterior
    public static boolean saveCalculatedDataIntoFile(int[][] diaryCovidDataInInterval, String temporalResolution, int indexDataType) throws IOException, ParseException {
        boolean savedFile = false;
        String fileName;

        if (wantSaveFile()) {
            System.out.println("Escreva o nome que deseja dar ao ficheiro a criar: ");
            fileName = READ.nextLine();
            saveCovidDataInCsv(diaryCovidDataInInterval, temporalResolution, indexDataType, fileName);
            savedFile = true;
        }

        return savedFile;
    }

    public static boolean wantSaveFile() {
        String choice;
        System.out.println("Deseja guardar um ficheiro com estes dados? (" + YES_CHOICE + "/" + NO_CHOICE + ")");
        choice = READ.nextLine().trim();
        while (!choice.equalsIgnoreCase(YES_CHOICE) && !choice.equalsIgnoreCase(NO_CHOICE)) {
            System.out.println("Insira uma resposta válida! (" + YES_CHOICE + "/" + NO_CHOICE + ")");
            choice = READ.nextLine().trim();
        }

        return choice.equalsIgnoreCase(YES_CHOICE);
    }

    //Função para preencher uma string com um determinado número de caracteres
    public static String fillString(char character, int quantity) {
        return String.valueOf(character).repeat(quantity);
    }

    //Função para ler e validar um data
    public static String readDate() {
        String date = "";
        boolean validDate = false;

        while (!validDate) {
            date = correctlyFormattedDate(READ.nextLine().trim());

            validDate = validDate(date);

            if (!validDate) {
                System.out.println("Data inválida! Introduza a data corretamente");
            }
        }

        return date;
    }

    //Função para ler e validar um ou mais intervalos de 2 datas
    public static String[] readDateRange(int numberIntervals) throws ParseException {
        String[] dateRangeValue = new String[numberIntervals * 2];
        int intervalNumber = 0;
        boolean validDateRange;

        for (var i = 0; i < dateRangeValue.length; i += 2) {
            if (numberIntervals > 1) {
                System.out.println((++intervalNumber) + "º Intervalo de datas");
            }

            validDateRange = false;

            while (!validDateRange) {
                System.out.println("Introduza a data inicial do intervalo (Formato AAAA-MM-DD ou DD-MM-AAAA):");
                dateRangeValue[i] = readDate();
                System.out.println("Introduza a data final do intervalo (Formato AAAA-MM-DD ou DD-MM-AAAA):");
                dateRangeValue[i + 1] = readDate();

                if (comparisonBetweenTwoDates(dateRangeValue[i], dateRangeValue[i + 1]) >= 0) {
                    validDateRange = true;
                } else {
                    System.out.println("Intervalo de datas inválido");
                }
            }
        }

        return dateRangeValue;
    }

    //Função para validar um data
    public static boolean validDate(String date) {
        int year, month, day;
        boolean validDate = false;

        if (date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            String[] departureDate = date.split("-");
            year = Integer.parseInt(departureDate[YEAR_INDEX]);
            month = Integer.parseInt(departureDate[MONTH_INDEX]);
            day = Integer.parseInt(departureDate[DAY_INDEX]);

            if (day >= 1 && day <= howManyDaysTheMonthHave(year, month)) {
                validDate = true;
            }
        }

        return validDate;
    }

    //Função para formatar corretamente uma data (DD-MM-AAAA para AAAA-MM-DD)
    public static String correctlyFormattedDate(String date) {
        if (date.matches("\\d{2}-\\d{2}-\\d{4}")) {
            String[] departureDate = date.split("-");
            String day = departureDate[0];
            String month = departureDate[1];
            String year = departureDate[2];

            date = year + "-" + month + "-" + day;
        }

        return date;
    }

    //Função para contar o número de dias entre duas datas
    public static int countNumberDaysBetweenTwoDates(String initialDate, String finalDate) throws ParseException {
        int numberDaysInterval = 0;

        Calendar calendarInitialDate = Calendar.getInstance();
        Calendar calendarFinalDate = Calendar.getInstance();

        calendarInitialDate.setTime(FORMAT_DATE.parse(initialDate));
        calendarFinalDate.setTime(FORMAT_DATE.parse(finalDate));

        numberDaysInterval += (calendarFinalDate.getTimeInMillis() - calendarInitialDate.getTimeInMillis()) / (24 * 60 * 60 * 1000);

        return numberDaysInterval;
    }

    //Função para comparar duas datas
    public static int comparisonBetweenTwoDates(String initialDate, String finalDate) throws ParseException {
        Calendar calendarInitialDate = Calendar.getInstance();
        Calendar calendarFinalDate = Calendar.getInstance();

        calendarInitialDate.setTime(FORMAT_DATE.parse(initialDate));
        calendarFinalDate.setTime(FORMAT_DATE.parse(finalDate));

        return calendarFinalDate.compareTo(calendarInitialDate);
    }

    //Função para juntar os valores inteiros de uma data em String
    public static String calendarToString(int year, int month, int day) {
        Calendar date = Calendar.getInstance();
        //Na classe calendar os meses são de 0 a 11
        date.set(year, month - 1, day);

        return FORMAT_DATE.format(date.getTime());
    }

    //Função para adicionar determinados dias a uma data
    public static String addDayData(String date, int numberDays) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(FORMAT_DATE.parse(date));
        calendar.add(Calendar.DATE, numberDays);

        return FORMAT_DATE.format(calendar.getTime());
    }

    //Função para criar um array com os valores que constam entres duas datas
    public static int[][] calculatePosition(int[][] diaryCovidData, int amountOfExistingData, String initialDate,
                                            String finalDate, boolean accumulatedData) throws ParseException {

        int countNumberDaysWithData = countNumberDaysWithDataBetweenTwoDates(diaryCovidData, amountOfExistingData, initialDate, finalDate, accumulatedData);
        int[][] diaryCovidDataInInterval = new int[countNumberDaysWithData][TOTAL_FIELDS];

        int lineCounter = 0;

        for (int line = 0; line < amountOfExistingData; line++) {
            String date = calendarToString(diaryCovidData[line][YEAR_INDEX], diaryCovidData[line][MONTH_INDEX], diaryCovidData[line][DAY_INDEX]);

            if (comparisonBetweenTwoDates(initialDate, date) >= 0 && comparisonBetweenTwoDates(date, finalDate) >= 0 && lineCounter < countNumberDaysWithData) {

                diaryCovidDataInInterval[lineCounter][YEAR_INDEX] = diaryCovidData[line][YEAR_INDEX];
                diaryCovidDataInInterval[lineCounter][MONTH_INDEX] = diaryCovidData[line][MONTH_INDEX];
                diaryCovidDataInInterval[lineCounter][DAY_INDEX] = diaryCovidData[line][DAY_INDEX];

                for (int column = START_INDEX_WITHOUT_DATE; column < TOTAL_FIELDS; column++) {
                    if (line > 0 && accumulatedData) {
                        diaryCovidDataInInterval[lineCounter][column] = calculateDifferenceBetweenTwoNumbers(diaryCovidData[line - 1][column],
                                diaryCovidData[line][column]);
                    } else {
                        diaryCovidDataInInterval[lineCounter][column] = diaryCovidData[line][column];
                    }
                }
                lineCounter++;

                if (line == 0 && accumulatedData) {
                    lineCounter--;
                }
            }
        }

        return diaryCovidDataInInterval;
    }

    //Função para contar o número de dias com dados no array entres duas datas
    public static int countNumberDaysWithDataBetweenTwoDates(int[][] diaryCovidData, int amountOfExistingData, String initialDate, String finalDate, boolean accumulatedData) throws ParseException {
        int countNumberDaysWithData = 0;

        for (int line = 0; line < amountOfExistingData; line++) {
            String date = calendarToString(diaryCovidData[line][YEAR_INDEX], diaryCovidData[line][MONTH_INDEX], diaryCovidData[line][DAY_INDEX]);

            if (comparisonBetweenTwoDates(initialDate, date) >= 0 && comparisonBetweenTwoDates(date, finalDate) >= 0) {
                countNumberDaysWithData++;

                if (line == 0 && accumulatedData) {
                    countNumberDaysWithData--;
                }
            }
        }

        return countNumberDaysWithData;
    }

    //Função para realizar a soma dos valores
    public static int[] sumValues(int[][] diaryCovidData) {
        int[] sum = new int[FIELDS_WITHOUT_DATE];

        for (int line = 0; line < diaryCovidData.length; line++) {
            for (int column = START_INDEX_WITHOUT_DATE; column < TOTAL_FIELDS; column++) {
                sum[column - START_INDEX_WITHOUT_DATE] += diaryCovidData[line][column];
            }
        }

        return sum;
    }

    //Função para realizar a diferença entre os dois periodos
    public static int[][] calculateDifference(int[][] diaryCovidDataPeriod1, int[][] diaryCovidDataPeriod2) {
        int lengthArray = Math.min(diaryCovidDataPeriod1.length, diaryCovidDataPeriod2.length);

        int[][] difference = new int[lengthArray][TOTAL_FIELDS];

        for (int line = 0; line < difference.length; line++) {
            for (int column = START_INDEX_WITHOUT_DATE; column < TOTAL_FIELDS; column++) {
                difference[line][column] = diaryCovidDataPeriod1[line][column] - diaryCovidDataPeriod2[line][column];
            }
        }

        return difference;
    }

    //Função para calcular a média
    public static double[] calculateAverage(int[][] diaryCovidData) {
        double[] average = new double[FIELDS_WITHOUT_DATE];

        int[] sumValues = sumValues(diaryCovidData);

        for (int i = 0; i < average.length; i++) {
            average[i] = (double) sumValues[i] / diaryCovidData.length;
        }

        return average;
    }

    //Função para calcular o desvio padrão
    public static double[] calculateStandardDeviation(int[][] diaryCovidData) {
        double[] average = calculateAverage(diaryCovidData);
        double[] standardDeviation = new double[FIELDS_WITHOUT_DATE];

        for (int line = 0; line < diaryCovidData.length; line++) {
            for (int column = START_INDEX_WITHOUT_DATE; column < TOTAL_FIELDS; column++) {
                standardDeviation[column - START_INDEX_WITHOUT_DATE] += Math.pow(diaryCovidData[line][column] - average[column - START_INDEX_WITHOUT_DATE], 2);
            }
        }

        for (int i = 0; i < standardDeviation.length; i++) {
            standardDeviation[i] = Math.sqrt(standardDeviation[i] / diaryCovidData.length);
        }

        return standardDeviation;
    }

    //Função para verificar se é possível realizar a análise comparativa
    public static boolean comparativeAnalysisOptions(int[][] diaryCovidDataPeriod1, int[][] diaryCovidDataPeriod2, String[] dateRange, int indexDataType) {
        boolean comparativeAnalysisAvailable = false;

        if (diaryCovidDataPeriod1.length == 0 || diaryCovidDataPeriod2.length == 0) {
            System.out.println("Não é possível realizar a análise comparativa por falta de dados.");
        } else {
            if (indexDataType == TOTAL_FIELDS) {
                for (int field = INFECTED_INDEX; field < TOTAL_FIELDS; field++) {
                    printComparativeAnalysisStructure(diaryCovidDataPeriod1, diaryCovidDataPeriod2, dateRange, field);
                    System.out.println();
                }
            } else {
                printComparativeAnalysisStructure(diaryCovidDataPeriod1, diaryCovidDataPeriod2, dateRange, indexDataType);
            }
            comparativeAnalysisAvailable = true;
        }

        System.out.println();

        return comparativeAnalysisAvailable;
    }

    //Procedimento para imprimir o cabeçalho da análise comparativa em relação à diferença entre 2 períodos
    public static void printHeaderComparativeAnalysisDifferences(int indexDataType) {
        switch (indexDataType) {
            case INFECTED_INDEX:
                System.out.printf("%18s %20s %18s %20s %20s %n", "DATA 1ºPERÍODO", "INFETADOS 1ºP.", "DATA 2ºPERÍODO", "INFETADOS 2ºP.", "DIFERENÇAS");
                break;
            case HOSPITALIZED_INDEX:
                System.out.printf("%18s %20s %18s %20s %20s %n", "DATA 1ºPERÍODO", "HOSPITALIZADOS 1ºP.", "DATA 2ºPERÍODO", "HOSPITALIZADOS 2ºP.", "DIFERENÇAS");
                break;
            case ICU_ADMITTED_INDEX:
                System.out.printf("%18s %20s %18s %20s %20s %n", "DATA 1ºPERÍODO", "INTERNADOS UCI 1ºP.", "DATA 2ºPERÍODO", "INTERNADOS UCI 2ºP.", "DIFERENÇAS");
                break;
            case CASUALTIES_INDEX:
                System.out.printf("%18s %20s %18s %20s %20s %n", "DATA 1ºPERÍODO", "MORTES 1ºP.", "DATA 2ºPERÍODO", "MORTES 2ºP.", "DIFERENÇAS");
                break;
        }
    }

    //Procedimento para retornar o cabeçalho da análise comparativa em relação à diferença entre 2 períodos para um ficheiro
    public static String printHeaderComparativeAnalysisDifferencesInCsv(int indexDataType) {
        String headerComparativeAnalysisDifferences = "";
        switch (indexDataType) {
            case INFECTED_INDEX:
                headerComparativeAnalysisDifferences = "dataPeriodo1,infetadosPeriodo1,dataPeriodo2,infetadosPeriodo2,diferencas\n";
                break;
            case HOSPITALIZED_INDEX:
                headerComparativeAnalysisDifferences = "dataPeriodo1,hospitalizadosPeriodo1,dataPeriodo2,hospitalizadosPeriodo2,diferencas\n";
                break;
            case ICU_ADMITTED_INDEX:
                headerComparativeAnalysisDifferences = "dataPeriodo1,internadosUCIPeriodo1,dataPeriodo2,internadosUCIPeriodo2,diferencas\n";
                break;
            case CASUALTIES_INDEX:
                headerComparativeAnalysisDifferences = "dataPeriodo1,obitosPeriodo1,dataPeriodo2,obitosPeriodo2,diferencas\n";
                break;
        }
        return headerComparativeAnalysisDifferences;
    }

    //Procedimento para imprimir a estrutura da funcionalidade da análise comparativa
    public static void printComparativeAnalysisStructure(int[][] diaryCovidDataPeriod1, int[][] diaryCovidDataPeriod2, String[] dates, int indexDataType) {
        String[] periodDates1 = {dates[0], dates[1]};
        String[] periodDates2 = {dates[2], dates[3]};

        int[][] difference = calculateDifference(diaryCovidDataPeriod1, diaryCovidDataPeriod2);

        System.out.println(SEPARATOR);
        System.out.printf("%60s %n", "ANÁLISE COMPARATIVA ENTRE " + periodDates1[0] + " A " + periodDates1[1] + " E " + periodDates2[0] + " A " + periodDates2[1]);
        System.out.println(SEPARATOR);
        printHeaderComparativeAnalysisDifferences(indexDataType);
        printComparativeAnalysisDifferences(difference, diaryCovidDataPeriod1, diaryCovidDataPeriod2, indexDataType);

        System.out.println(SEPARATOR);
        System.out.printf("%25s %20s %20s %20s %n", "TIPO", "1ºPERÍODO", "2ºPERÍODO", "DIFERENÇAS");
        System.out.println(SEPARATOR);
        printComparativeAnalysis("MÉDIA", calculateAverage(diaryCovidDataPeriod1)[indexDataType - START_INDEX_WITHOUT_DATE], calculateAverage(diaryCovidDataPeriod2)[indexDataType - START_INDEX_WITHOUT_DATE], calculateAverage(difference)[indexDataType - START_INDEX_WITHOUT_DATE]);
        System.out.println(SECOND_SEPARATOR);
        printComparativeAnalysis("DESVIO PADRÃO", calculateStandardDeviation(diaryCovidDataPeriod1)[indexDataType - START_INDEX_WITHOUT_DATE], calculateStandardDeviation(diaryCovidDataPeriod2)[indexDataType - START_INDEX_WITHOUT_DATE], calculateStandardDeviation(difference)[indexDataType - START_INDEX_WITHOUT_DATE]);
        System.out.println(SEPARATOR);
    }

    //Procedimento para imprimir a informação da análise comparativa em relação à diferença entre 2 períodos
    public static void printComparativeAnalysisDifferences(int[][] difference, int[][] diaryCovidDataPeriod1,
                                                           int[][] diaryCovidDataPeriod2, int indexDataType) {
        System.out.println(SEPARATOR);
        for (int line = 0; line < difference.length; line++) {
            String initialDate = calendarToString(diaryCovidDataPeriod1[line][YEAR_INDEX], diaryCovidDataPeriod1[line][MONTH_INDEX], diaryCovidDataPeriod1[line][DAY_INDEX]);
            String finalDate = calendarToString(diaryCovidDataPeriod2[line][YEAR_INDEX], diaryCovidDataPeriod2[line][MONTH_INDEX], diaryCovidDataPeriod2[line][DAY_INDEX]);

            System.out.printf("%18s %20d %18s %20d", initialDate, diaryCovidDataPeriod1[line][indexDataType], finalDate, diaryCovidDataPeriod2[line][indexDataType]);

            if (difference[line][indexDataType] > 0) {
                System.out.printf(" %20s", "+" + difference[line][indexDataType]);
            } else {
                System.out.printf(" %20d", difference[line][indexDataType]);
            }
            System.out.printf("%n");

            if (line != difference.length - 1) {
                System.out.println(SECOND_SEPARATOR);
            }
        }

        System.out.println(SEPARATOR);
    }

    //Procedimento para imprimir a informação da análise comparativa em relação à média e desvio padrão de cada período
    public static void printComparativeAnalysis(String typeComparativeAnalysis, double arrayAnalysisPeriod1, double arrayAnalysisPeriod2, double arrayAnalysisDifferences) {
        System.out.printf("%25s %20.4f %20.4f %20.4f", typeComparativeAnalysis, arrayAnalysisPeriod1, arrayAnalysisPeriod2, arrayAnalysisDifferences);
        System.out.printf("%n");
    }

    //Procedimento para guardar os dados da análise comparativa num ficheiro
    public static void saveComparativeAnalysisInCsv(int[][] diaryCovidDataPeriod1, int[][] diaryCovidDataPeriod2, int indexDataType, String fileName) throws IOException {
        int[][] difference = calculateDifference(diaryCovidDataPeriod1, diaryCovidDataPeriod2);

        double[] averagePeriod1 = calculateAverage(diaryCovidDataPeriod1);
        double[] averagePeriod2 = calculateAverage(diaryCovidDataPeriod2);
        double[] averageDifference = calculateAverage(difference);

        double[] standardDeviationPeriod1 = calculateStandardDeviation(diaryCovidDataPeriod1);
        double[] standardDeviationPeriod2 = calculateStandardDeviation(diaryCovidDataPeriod2);
        double[] standardDeviationDifference = calculateStandardDeviation(difference);

        if (indexDataType == TOTAL_FIELDS) {
            for (int field = INFECTED_INDEX - START_INDEX_WITHOUT_DATE; field < FIELDS_WITHOUT_DATE; field++) {
                printComparativeAnalysisDifferencesInCsv(difference, diaryCovidDataPeriod1, diaryCovidDataPeriod2, field + START_INDEX_WITHOUT_DATE, fileName);
                printComparativeAnalysisInCsv("media", averagePeriod1[field], averagePeriod2[field], averageDifference[field], fileName, FOUR_DECIMAL_PLACE);
                printComparativeAnalysisInCsv("desvioPadrao", standardDeviationPeriod1[field], standardDeviationPeriod2[field], standardDeviationDifference[field], fileName, FOUR_DECIMAL_PLACE);
            }
        } else {
            printComparativeAnalysisDifferencesInCsv(difference, diaryCovidDataPeriod1, diaryCovidDataPeriod2, indexDataType, fileName);
            printComparativeAnalysisInCsv("media", averagePeriod1[indexDataType - START_INDEX_WITHOUT_DATE], averagePeriod2[indexDataType - START_INDEX_WITHOUT_DATE], averageDifference[indexDataType - START_INDEX_WITHOUT_DATE], fileName, FOUR_DECIMAL_PLACE);
            printComparativeAnalysisInCsv("desvioPadrao", standardDeviationPeriod1[indexDataType - START_INDEX_WITHOUT_DATE], standardDeviationPeriod2[indexDataType - START_INDEX_WITHOUT_DATE], standardDeviationDifference[indexDataType - START_INDEX_WITHOUT_DATE], fileName, FOUR_DECIMAL_PLACE);
        }
    }

    //Procedimento para imprimir num ficheiro a informação da análise comparativa em relação à diferença entre 2 períodos
    public static void printComparativeAnalysisDifferencesInCsv(int[][] difference, int[][] diaryCovidDataPeriod1,
                                                                int[][] diaryCovidDataPeriod2, int indexDataType, String fileName) throws IOException {

        PrintWriter fWriter = new PrintWriter(new FileWriter(fileName, true));

        fWriter.printf(printHeaderComparativeAnalysisDifferencesInCsv(indexDataType));

        for (int line = 0; line < difference.length; line++) {
            String initialDate = calendarToString(diaryCovidDataPeriod1[line][YEAR_INDEX], diaryCovidDataPeriod1[line][MONTH_INDEX], diaryCovidDataPeriod1[line][DAY_INDEX]);
            String finalDate = calendarToString(diaryCovidDataPeriod2[line][YEAR_INDEX], diaryCovidDataPeriod2[line][MONTH_INDEX], diaryCovidDataPeriod2[line][DAY_INDEX]);

            fWriter.printf("%s,%d,%s,%d,%d %n", initialDate, diaryCovidDataPeriod1[line][indexDataType], finalDate, diaryCovidDataPeriod2[line][indexDataType], difference[line][indexDataType]);

        }

        fWriter.close();
    }

    //Procedimento para imprimir num ficheiro a informação da análise comparativa em relação à média e desvio padrão de cada período
    public static void printComparativeAnalysisInCsv(String typeComparativeAnalysis, double analysisPeriod1, double analysisPeriod2, double analysisDifferences,
                                                     String fileName, double decimalPlaces) throws IOException {
        PrintWriter fWriter = new PrintWriter(new FileWriter(fileName, true));

        fWriter.printf("%s,%s,%s,%s %n", typeComparativeAnalysis, String.valueOf(Math.round(analysisPeriod1 * decimalPlaces) / decimalPlaces).replace(",", "."),
                String.valueOf(Math.round(analysisPeriod2 * decimalPlaces) / decimalPlaces).replace(",", "."),
                String.valueOf(Math.round(analysisDifferences * decimalPlaces) / decimalPlaces).replace(",", "."));

        fWriter.close();
    }

    //Função para calcular a diferença entre dois números
    public static int calculateDifferenceBetweenTwoNumbers(int firstMeasurement, int secondMesurement) {
        return secondMesurement - firstMeasurement;
    }

    //Procedimento para imprimir o cabeçalho da análise a diferente resoluções temporais
    public static void printHeaderTemporalResolution(int indexDataType) {
        switch (indexDataType) {
            case INFECTED_INDEX:
                System.out.println(SEPARATOR_ONE_FIELD);
                System.out.printf("%-24s %18s %n", "DATA", "INFETADOS");
                System.out.println(SEPARATOR_ONE_FIELD);
                break;
            case HOSPITALIZED_INDEX:
                System.out.println(SEPARATOR_ONE_FIELD);
                System.out.printf("%-24s %18s %n", "DATA", "HOSPITALIZADOS");
                System.out.println(SEPARATOR_ONE_FIELD);
                break;
            case ICU_ADMITTED_INDEX:
                System.out.println(SEPARATOR_ONE_FIELD);
                System.out.printf("%-24s %18s %n", "DATA", "INTERNADOS UCI");
                System.out.println(SEPARATOR_ONE_FIELD);
                break;
            case CASUALTIES_INDEX:
                System.out.println(SEPARATOR_ONE_FIELD);
                System.out.printf("%-24s %18s %n", "DATA", "MORTES");
                System.out.println(SEPARATOR_ONE_FIELD);
                break;
            case TOTAL_FIELDS:
                System.out.println(SEPARATOR);
                System.out.printf("%-24s %18s %18s %18s %18s %n", "DATA", "INFETADOS", "HOSPITALIZADOS", "INTERNADOS UCI", "MORTES");
                System.out.println(SEPARATOR);
                break;
        }
    }

    //Procedimento para retornar o cabeçalho da análise comparativa em relação à diferença entre 2 períodos para um ficheiro
    public static String printHeaderTemporalResolutionInCsv(int indexDataType) {
        String headerTemporalResolution = "";
        switch (indexDataType) {
            case INFECTED_INDEX:
                headerTemporalResolution = "data,infetados\n";
                break;
            case HOSPITALIZED_INDEX:
                headerTemporalResolution = "data,hospitalizados\n";
                break;
            case ICU_ADMITTED_INDEX:
                headerTemporalResolution = "data,internadosUCI\n";
                break;
            case CASUALTIES_INDEX:
                headerTemporalResolution = "data,obitos\n";
                break;
            case TOTAL_FIELDS:
                headerTemporalResolution = "data,infetados,hospitalizados,internadosUCI,obitos\n";
                break;
        }
        return headerTemporalResolution;
    }

    //Método para realizar a impressão dos dados na análise a diferente resoluções temporais
    public static void printDataTemporalResolution(int[][] calculatedMesurements, String temporalResolution, int indexDataType) throws ParseException {
        printHeaderTemporalResolution(indexDataType);

        for (int line = 0; line < calculatedMesurements.length; line++) {
            if (calculatedMesurements[line][MONTH_INDEX] > 0 && calculatedMesurements[line][DAY_INDEX] > 0) {
                String date = calendarToString(calculatedMesurements[line][YEAR_INDEX], calculatedMesurements[line][MONTH_INDEX], calculatedMesurements[line][DAY_INDEX]);

                switch (temporalResolution) {
                    case WEEKLY_TYPE:
                        date += " - " + addDayData(date, TOTAL_WEEK_DAYS - 1);
                        break;
                    case MONTHLY_TYPE:
                        date += " - " + addDayData(date, howManyDaysTheMonthHave(calculatedMesurements[line][YEAR_INDEX], calculatedMesurements[line][MONTH_INDEX]) - 1);
                        break;
                }

                System.out.printf("%-24s", date);

                if (indexDataType == TOTAL_FIELDS) {
                    for (int column = START_INDEX_WITHOUT_DATE + 1; column < calculatedMesurements[line].length; column++) {
                        System.out.printf(" %18d", calculatedMesurements[line][column]);
                    }
                    System.out.printf("%n");
                } else {
                    System.out.printf(" %18d %n", calculatedMesurements[line][indexDataType]);
                }

                if (line != calculatedMesurements.length - 1) {
                    if (indexDataType == TOTAL_FIELDS) {
                        System.out.println(SECOND_SEPARATOR);
                    } else {
                        System.out.println(SECOND_SEPARATOR_ONE_FIELD);
                    }
                }
            }
        }

        if (indexDataType == TOTAL_FIELDS) {
            System.out.println(SEPARATOR);
        } else {
            System.out.println(SEPARATOR_ONE_FIELD);
        }
    }

    //Procedimento para guardar os dados do covid num ficheiro CSV
    public static void saveCovidDataInCsv(int[][] diaryCovidDataInInterval, String temporalResolution, int indexDataType, String fileName) throws IOException, ParseException {
        PrintWriter fWriter = new PrintWriter(new FileWriter(fileName, true));

        fWriter.printf(printHeaderTemporalResolutionInCsv(indexDataType));

        for (int line = 0; line < diaryCovidDataInInterval.length; line++) {
            if (diaryCovidDataInInterval[line][MONTH_INDEX] > 0 && diaryCovidDataInInterval[line][DAY_INDEX] > 0) {
                String date = calendarToString(diaryCovidDataInInterval[line][YEAR_INDEX], diaryCovidDataInInterval[line][MONTH_INDEX], diaryCovidDataInInterval[line][DAY_INDEX]);

                switch (temporalResolution) {
                    case WEEKLY_TYPE:
                        date += " - " + addDayData(date, TOTAL_WEEK_DAYS - 1);
                        break;
                    case MONTHLY_TYPE:
                        date += " - " + addDayData(date, howManyDaysTheMonthHave(diaryCovidDataInInterval[line][YEAR_INDEX], diaryCovidDataInInterval[line][MONTH_INDEX]) - 1);
                        break;
                }

                fWriter.printf("%s", date);
                if (indexDataType == TOTAL_FIELDS) {
                    for (int column = START_INDEX_WITHOUT_DATE + 1; column < diaryCovidDataInInterval[line].length; column++) {
                        fWriter.printf(",%d", diaryCovidDataInInterval[line][column]);
                    }
                } else {
                    fWriter.printf(",%d", diaryCovidDataInInterval[line][indexDataType]);
                }
                fWriter.printf("%n");
            }
        }

        fWriter.close();
    }

    // Identificar qual é o dia da semana de uma certa data
    public static int identifyWeekDay(String date) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(FORMAT_DATE.parse(date));

        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    //Analise semanal
    public static int[][] showWeeklyAnalysis(int[][] diaryCovidDataInInterval) throws ParseException {
        int initialIndexArray = findIndexOfFirstDayOccurrence(diaryCovidDataInInterval, 2);
        int finalIndexArray = findIndexOfLastDayOccurrence(diaryCovidDataInInterval.length, TOTAL_WEEK_DAYS, initialIndexArray);

        String initialDate = calendarToString(diaryCovidDataInInterval[initialIndexArray][YEAR_INDEX], diaryCovidDataInInterval[initialIndexArray][MONTH_INDEX], diaryCovidDataInInterval[initialIndexArray][DAY_INDEX]);
        String finalDate = calendarToString(diaryCovidDataInInterval[finalIndexArray][YEAR_INDEX], diaryCovidDataInInterval[finalIndexArray][MONTH_INDEX], diaryCovidDataInInterval[finalIndexArray][DAY_INDEX]);

        return calculatePosition(diaryCovidDataInInterval, diaryCovidDataInInterval.length, initialDate, finalDate, false);
    }

    // Encontra o índice da primeira ocorrência de um dia da semana, ou seja, de domingo a segunda-feira
    public static int findIndexOfFirstDayOccurrence(int[][] dateInterval, int neededDay) throws ParseException {
        String initialDate = calendarToString(dateInterval[0][YEAR_INDEX], dateInterval[0][MONTH_INDEX], dateInterval[0][DAY_INDEX]);
        int initialIndex = 0;

        //Para encontrar a posição no array da primeira segunda-feira
        while (identifyWeekDay(initialDate) != neededDay) {
            initialDate = addDayData(initialDate, 1);
            initialIndex++;
        }

        return initialIndex;
    }

    // Encontra o índice da última ocorrência do dia da semana anterior ao dia correspondente ao índice passado por
    //parâmetro. Por exemplo, se for passado um valor de índice de uma segunda-feira e for passado que queremos
    //percorrer o array de 7 em 7 (correspondente aos dias), será devolvido o índice do último domingo existente no
    //array.
    public static int findIndexOfLastDayOccurrence(int totalDaysWithData, int intervalDays, int firstDayIndex) {
        int finalDayIndex = firstDayIndex;

        while (finalDayIndex + intervalDays <= totalDaysWithData) {
            finalDayIndex += intervalDays;
        }

        // -1 porque os cálculos são feitos de segunda a segunda e como último queremos apenas domingo
        return finalDayIndex - 1;
    }

    // Encontrar o índice da primeira ocorrência de um mês completo num intervalo de datas
    public static int findIndexOfTheFirstDayOfTheFirstFullMonth(int[][] dateInterval, int arrayLengthWithData) {
        boolean foundFirstFullMonth = false;
        int day = 0;
        int indexFirstFullMonthDay = -1;

        while (!foundFirstFullMonth && day < arrayLengthWithData) {
            if (itsFirstDayAndHaveFullMonth(dateInterval, day, arrayLengthWithData)) {
                foundFirstFullMonth = true;
                indexFirstFullMonthDay = day;
            } else {
                day = findNextMonthIndex(dateInterval, day);
            }
        }

        return indexFirstFullMonthDay;
    }

    // Encontrar o índice do último dia da última ocorrência de um mês completo num intervalo de datas
    public static int findIndexOfTheLastDayOfTheLastFullMonth(int[][] dateInterval, int arrayLengthWithData) {
        int day = 0;
        int lastFullMonthIndex = -1;

        while (day < arrayLengthWithData) {
            if (itsFirstDayAndHaveFullMonth(dateInterval, day, arrayLengthWithData)) {
                lastFullMonthIndex = findNextMonthIndex(dateInterval, day) - 1;
            }
            day = findNextMonthIndex(dateInterval, day);
        }

        return lastFullMonthIndex;
    }

    // Método para construir um array com apenas dados de meses completos (com dados de todos os dias desse mesmo mês)
    public static int[][] buildArrayWithFullMonthlyIntervals(int[][] diaryCovidDataInInterval) throws ParseException {
        int[][] finalNeededInterval = null;

        int firstDayIndex = findIndexOfTheFirstDayOfTheFirstFullMonth(diaryCovidDataInInterval, diaryCovidDataInInterval.length);
        int lastDayIndex;

        if (firstDayIndex > -1) {
            lastDayIndex = findIndexOfTheLastDayOfTheLastFullMonth(diaryCovidDataInInterval, diaryCovidDataInInterval.length);
            if (lastDayIndex > -1) {
                String initialDate = calendarToString(diaryCovidDataInInterval[firstDayIndex][YEAR_INDEX], diaryCovidDataInInterval[firstDayIndex][MONTH_INDEX], diaryCovidDataInInterval[firstDayIndex][DAY_INDEX]);
                String finalDate = calendarToString(diaryCovidDataInInterval[lastDayIndex][YEAR_INDEX], diaryCovidDataInInterval[lastDayIndex][MONTH_INDEX], diaryCovidDataInInterval[lastDayIndex][DAY_INDEX]);
                finalNeededInterval = calculatePosition(diaryCovidDataInInterval, diaryCovidDataInInterval.length, initialDate, finalDate, false);
            }
        }

        return finalNeededInterval;

    }

    // Método para descobrir qual é o possível índice do primeiro dia do próximo mês
    public static int findNextMonthIndex(int[][] dateInterval, int actualIndex) {
        int amountOfDaysActualMonth = howManyDaysTheMonthHave(dateInterval[actualIndex][YEAR_INDEX], dateInterval[actualIndex][MONTH_INDEX]);
        actualIndex += (amountOfDaysActualMonth - dateInterval[actualIndex][DAY_INDEX]) + 1;
        return actualIndex;
    }

    // Método para saber se o valor de um determinado índice do array é o primeiro dia de um mês e se este mesmo array
    // contem todos os restantes dias desse mesmo mês (se é um mês completo)
    public static boolean itsFirstDayAndHaveFullMonth(int[][] dateInterval, int startingDay, int arrayLengthWithData) {
        boolean itsFirstDayAndHaveFullMonth = false;
        if (startingDay >= 0 && startingDay < arrayLengthWithData) {
            int amountOfDaysActualMonth = howManyDaysTheMonthHave(dateInterval[startingDay][YEAR_INDEX], dateInterval[startingDay][MONTH_INDEX]);
            itsFirstDayAndHaveFullMonth = dateInterval[startingDay][DAY_INDEX] == 1
                    && (startingDay + amountOfDaysActualMonth - 1 < arrayLengthWithData);
        }
        return itsFirstDayAndHaveFullMonth;
    }

    // Método para contar quantos meses inteiros existem num dado intervalo de datas
    public static int countFullMonthsOnAInterval(int[][] dateInterval, int arrayLengthWithData) {

        int fullMonths = 0, actualDay = 0;

        while (actualDay < arrayLengthWithData) {

            if (itsFirstDayAndHaveFullMonth(dateInterval, actualDay, arrayLengthWithData)) {
                fullMonths++;
            }

            actualDay = findNextMonthIndex(dateInterval, actualDay);

        }

        return fullMonths;

    }

    // Método para calcular a soma das estatísticas diárias
    public static int[][] calculateSumOfEveryDayStatistics(int[][] diaryCovidDataInInterval,
                                                           int numberOfLines, String typeSum) {
        int[][] sumOfEveryDayStatistics = new int[numberOfLines][TOTAL_FIELDS];
        int totalIterations = 0, day = 0, intervalLength = 0;

        while (totalIterations < numberOfLines) {
            sumOfEveryDayStatistics[totalIterations][YEAR_INDEX] = diaryCovidDataInInterval[day][YEAR_INDEX];
            sumOfEveryDayStatistics[totalIterations][MONTH_INDEX] = diaryCovidDataInInterval[day][MONTH_INDEX];
            sumOfEveryDayStatistics[totalIterations][DAY_INDEX] = diaryCovidDataInInterval[day][DAY_INDEX];

            if (typeSum.equals(WEEKLY_TYPE)) {
                intervalLength = day + 7;
            } else if (typeSum.equals(MONTHLY_TYPE)) {
                intervalLength = day + howManyDaysTheMonthHave(sumOfEveryDayStatistics[totalIterations][YEAR_INDEX], sumOfEveryDayStatistics[totalIterations][MONTH_INDEX]);
            }

            while (day < intervalLength) {
                for (int data = START_INDEX_WITHOUT_DATE; data < TOTAL_FIELDS; data++) {
                    sumOfEveryDayStatistics[totalIterations][data] += diaryCovidDataInInterval[day][data];
                }
                day++;
            }

            totalIterations++;
        }

        return sumOfEveryDayStatistics;
    }

    //Função para saber quantos dias tem o mês
    public static int howManyDaysTheMonthHave(int year, int month) {
        int[] daysOfEachMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int[] daysOfEachMonthLeap = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        if (month < 1 || month > 12) {
            return 0;
        }

        return isLeapYear(year) ? daysOfEachMonthLeap[month - 1] : daysOfEachMonth[month - 1];
    }

    //Função para saber se o ano é bissexto
    public static boolean isLeapYear(int year) {
        return year % 400 == 0 || (year % 4 == 0 && year % 100 != 0);
    }

    //Função para verificar se um ficheiro existe
    public static boolean fileAlreadyExists(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    //Subtrai uma matriz a outra matriz desde que sejam da mesma ordem
    public static double[][] subtractTwoMatrices(double[][] minuedMatrix, double[][] subtrahendMatrix) {
        double[][] differenceMatrix = new double[subtrahendMatrix.length][subtrahendMatrix[0].length];

        for (int i = 0; i < subtrahendMatrix.length; i++) {
            for (int j = 0; j < subtrahendMatrix[0].length; j++) {
                differenceMatrix[i][j] = minuedMatrix[i][j] - subtrahendMatrix[i][j];
            }
        }

        return differenceMatrix;
    }

    //Cria uma matriz identidade de ordem especificada
    public static double[][] createIdentityMatrix(int matrixOrder) {
        double[][] identityMatrix = new double[matrixOrder][matrixOrder];

        for (int i = 0; i < identityMatrix.length; i++) {
            for (int j = 0; j < identityMatrix[0].length; j++) {
                if (i == j) {
                    identityMatrix[i][j] = 1;
                }
            }
        }

        return identityMatrix;
    }

    //Método para preparar a leitura da matriz de transição de um ficheiro com uma dada extensão
    public static boolean readFileTransitionMatrix(double[][] transitionMatrix, String fileName, String extension) throws FileNotFoundException {
        boolean readFileTransitionMatrix = false;

        if (fileName.length() > extension.length()) {
            String fileExtension = fileName.substring(fileName.length() - extension.length());

            if (fileAlreadyExists(fileName)) {
                if (extension.equalsIgnoreCase(fileExtension)) {
                    if (createTransitionMatrix(transitionMatrix, fileName)) {
                        readFileTransitionMatrix = true;
                        System.out.println("Matriz de transição importada com sucesso!");
                    } else {
                        System.out.println("A matriz de transição encontra-se errada!");
                    }

                } else {
                    System.out.println("O ficheiro indicado não contém a extensão pretendida (" + extension + ")!");
                }
            } else {
                System.out.println("O ficheiro indicado não existe!");
            }
        } else {
            System.out.println("O nome do ficheiro é inválido!");
        }

        return readFileTransitionMatrix;
    }

    //Função para criar a matriz de transição através de um ficheiro
    public static boolean createTransitionMatrix(double[][] transitionMatrix, String fileName) throws FileNotFoundException {
        int line = 0, column = 0;
        boolean createTransitionMatrix = false;

        if (fileAlreadyExists(fileName)) {
            Scanner readFile = new Scanner(new File(fileName));

            while (readFile.hasNextLine()) {
                String nextLine = readFile.nextLine();

                if (!nextLine.isEmpty()) {
                    String[] lineValues = nextLine.split("=");
                    transitionMatrix[line][column] = Double.parseDouble(lineValues[1]);

                    if (column < transitionMatrix[line].length - 1) {
                        column++;
                    } else {
                        line++;
                        column = 0;
                    }
                }
            }

            createTransitionMatrix = Arrays.equals(sumOfMatrixColumns(transitionMatrix, transitionMatrix.length), SUM_OF_TRANSITION_MATRIX_COLUMNS);

            readFile.close();
        }

        return createTransitionMatrix;
    }

    //Cria a matriz de transição sem o estado absorvente
    public static double[][] createTransitionMatrixWithoutAbsorbingState(double[][] originalMatrix) {

        double[][] transitionMatrixWithoutAbsorbingState = null;

        if (originalMatrix != null && originalMatrix.length == originalMatrix[0].length && originalMatrix.length >= 3) {
            int lineLengthToCopy = originalMatrix.length - 1;
            int columnLengthToCopy = originalMatrix[0].length - 1;
            transitionMatrixWithoutAbsorbingState = new double[lineLengthToCopy][columnLengthToCopy];

            for (int line = 0; line < lineLengthToCopy; line++) {
                for (int column = 0; column < columnLengthToCopy; column++) {
                    transitionMatrixWithoutAbsorbingState[line][column] = originalMatrix[line][column];
                }
            }
        }

        return transitionMatrixWithoutAbsorbingState;
    }

    //Função para criar um array com o histórico de dados mais próximo do dia a prever
    public static int[] lastDayData(int[][] covidTotalDailyData, int countTotalDataRecords, String forecastDate) throws ParseException {
        boolean lastDayDataExist = false;
        int[] lastDayData = new int[TOTAL_FIELDS];

        for (int line = 0; line < countTotalDataRecords; line++) {
            String date = calendarToString(covidTotalDailyData[line][YEAR_INDEX], covidTotalDailyData[line][MONTH_INDEX], covidTotalDailyData[line][DAY_INDEX]);

            if (comparisonBetweenTwoDates(date, forecastDate) > 0) {

                lastDayDataExist = true;

                for (int column = 0; column < TOTAL_FIELDS; column++) {
                    lastDayData[column] = covidTotalDailyData[line][column];
                }
            }
        }

        return lastDayDataExist ? lastDayData : null;
    }

    //Função para calcular a previsão para o dia seguinte
    public static double[] calculateTheForecastForTheNextDay(double[] lastDayData, double[][] transitionMatrix) {
        double[] forecastForTheNextDay = null;

        if (lastDayData.length == transitionMatrix.length && lastDayData.length == transitionMatrix[0].length) {
            forecastForTheNextDay = new double[lastDayData.length];
            int size = lastDayData.length;

            for (int line = 0; line < size; line++) {
                for (int column = 0; column < size; column++) {
                    forecastForTheNextDay[line] += lastDayData[column] * transitionMatrix[line][column];
                }
            }
        }

        return forecastForTheNextDay;
    }

    //Função para prever os dados para um determinado dia
    public static double[] predictCovidValues(int[] lastDayData, double[][] transitionMatrix, String forecastDate) throws ParseException {
        double[] forecastForTheNextDay = {lastDayData[UNINFECTED_INDEX], lastDayData[INFECTED_INDEX], lastDayData[HOSPITALIZED_INDEX],
                lastDayData[ICU_ADMITTED_INDEX], lastDayData[CASUALTIES_INDEX]};

        String dateLastDayData = calendarToString(lastDayData[YEAR_INDEX], lastDayData[MONTH_INDEX], lastDayData[DAY_INDEX]);
        int numberDaysUntilForecast = countNumberDaysBetweenTwoDates(dateLastDayData, forecastDate);

        for (int i = 0; i < numberDaysUntilForecast; i++) {
            forecastForTheNextDay = calculateTheForecastForTheNextDay(forecastForTheNextDay, transitionMatrix);
        }

        return forecastForTheNextDay;
    }

    //Procedimento para imprimir os dados de uma previsão
    public static void printPredictCovidValues(double[] forecastForTheNextDay, String forecastDate) {
        System.out.println(SEPARATOR_PREDICT_COVID_VALUES);
        System.out.printf("%20s %20s %20s %20s %20s %20s %n", "DATA DA PREVISÃO", "NÃO INFETADOS", "INFETADOS", "HOSPITALIZADOS", "INTERNADOS UCI", "MORTES");
        System.out.println(SEPARATOR_PREDICT_COVID_VALUES);

        System.out.printf("%20s", forecastDate);
        for (int data = 0; data < forecastForTheNextDay.length; data++) {
            System.out.printf(" %20.1f", forecastForTheNextDay[data]);
        }
        System.out.printf("%n");
        System.out.println(SEPARATOR_PREDICT_COVID_VALUES);
    }

    //Procedimento para guardar os dados de uma previsão num ficheiro
    public static void savePredictCovidValuesInCsv(double[] forecastForTheNextDay, String forecastDate, String fileName, double decimalPlaces) throws IOException {
        PrintWriter fWriter = new PrintWriter(new FileWriter(fileName, true));

        fWriter.printf("dataPrevisao,naoInfetados,infetados,hospitalizados,internadosUCI,obitos\n");
        fWriter.printf("%s", forecastDate);

        for (int data = 0; data < forecastForTheNextDay.length; data++) {
            fWriter.printf(",%s", String.valueOf(Math.round(forecastForTheNextDay[data] * decimalPlaces) / decimalPlaces).replace(",", "."));
        }

        fWriter.printf("%n");
        fWriter.close();
    }

    //Calcula a quantidade esperada de dias que um individuo demora a morrer
    public static double[] theFinalCountdown(double[][] transitionMatrix) {
        double[][] transitionMatrixWithoutAbsorbingState = createTransitionMatrixWithoutAbsorbingState(transitionMatrix);
        double[][] matrixOfDaysUntilDeath;
        double[][] identityMatrix = createIdentityMatrix(transitionMatrixWithoutAbsorbingState.length);
        double[][] subtractedMatrix;
        double[] amountOfDaysUntilDeath;

        //Primeiro passo da fórmula - subtrair a matriz de transição sem o estado absorvente à matriz de identidade
        subtractedMatrix = subtractTwoMatrices(identityMatrix, transitionMatrixWithoutAbsorbingState);

        //Segundo passo da fórmula - inverter o resultado da subtração
        matrixOfDaysUntilDeath = inverseMatrix(subtractedMatrix);

        // Soma de todas as colunas da matriz
        amountOfDaysUntilDeath = sumOfMatrixColumns(matrixOfDaysUntilDeath, matrixOfDaysUntilDeath.length);

        return amountOfDaysUntilDeath;
    }

    //Soma os valores das colunas por linha
    public static double[] sumOfMatrixColumns(double[][] matrix, int numberMatrixColumns) {
        double[] sumOfMatrixColumns = new double[numberMatrixColumns];

        for (int column = 0; column < numberMatrixColumns; column++) {
            for (int line = 0; line < matrix[column].length; line++) {
                sumOfMatrixColumns[column] += matrix[line][column];
            }
        }

        return sumOfMatrixColumns;
    }

    //Multiplicação de 2 matrizes
    public static double[][] multiplicationMatrix(double[][] matrix1, double[][] matrix2) {
        double[][] matrixMultiplied = new double[matrix1.length][matrix2[0].length];
        for (int i = 0; i < matrix1.length; i++) {
            for (int j = 0; j < matrix2[0].length; j++) {
                matrixMultiplied[i][j] = 0;
                for (int k = 0; k < matrix1.length; k++) {
                    matrixMultiplied[i][j] += matrix1[i][k] * matrix2[k][j];
                }
            }
        }
        return matrixMultiplied;
    }

    //Inverte a matrix
    public static double[][] inverseMatrix(double[][] matrixToInvert) {

        int i, j;
        double[][] initialArray = copyMatrix(matrixToInvert, matrixToInvert.length);
        int length = matrixToInvert.length;
        double[] col = new double[length];

        double[][] inverseMatrix = new double[length][length];

        double trades = luDecomposition(initialArray);

        for (j = 0; j < length; j++) {
            for (i = 0; i < length; i++) {
                col[i] = 0.0;
            }

            col[j] = 1.0;
            backwardsSubstitution(initialArray, length, col);

            for (i = 0; i < length; i++) {
                inverseMatrix[i][j] = col[i];
            }
        }

        return inverseMatrix;
    }

    //Decompõe a matriz em L e U
    public static double luDecomposition(double[][] matrixToDecompose) {
        int imax = 0;
        int n = matrixToDecompose.length;
        double big = 0, dum, sum, temp;
        double[] vv = new double[n];
        double d = 1.0;
        double[][] upperTriangle = new double[matrixToDecompose.length][matrixToDecompose[0].length];
        double[][] lowerTriangle = new double[matrixToDecompose.length][matrixToDecompose[0].length];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if ((temp = Math.abs(matrixToDecompose[i][j])) > big) {
                    big = temp;
                }
            }
            vv[i] = 1.0 / big;
        }

        for (int j = 0; j < n; j++) {
            //Calcula a matriz triangular superior
            for (int i = 0; i < j; i++) {
                sum = matrixToDecompose[i][j];
                for (int k = 0; k < i; k++) {
                    sum -= matrixToDecompose[i][k] * matrixToDecompose[k][j];
                }
                matrixToDecompose[i][j] = sum;
            }
            big = 0.0;

            upperTriangle = copyMatrix(matrixToDecompose, matrixToDecompose.length);

            //Calcula a matriz triangular inferior
            for (int i = j; i < n; i++) {

                sum = matrixToDecompose[i][j];

                for (int k = 0; k < j; k++) {
                    sum -= matrixToDecompose[i][k] * matrixToDecompose[k][j];
                }

                matrixToDecompose[i][j] = sum;
                dum = vv[i] * Math.abs(sum);

                if (dum >= big) {
                    big = dum;
                    imax = i;
                }
            }

            lowerTriangle = copyMatrix(matrixToDecompose, matrixToDecompose.length);

            if (j != imax) {
                for (int k = 0; k < n; k++) {
                    dum = matrixToDecompose[imax][k];
                    matrixToDecompose[imax][k] = matrixToDecompose[j][k];
                    matrixToDecompose[j][k] = dum;
                }
                d = -(d); //...and change the parity of d.
                vv[imax] = vv[j];
            }

            if (matrixToDecompose[j][j] == 0.0) {
                matrixToDecompose[j][j] = TINY;
            }

            if (j != n) {
                dum = 1.0 / (matrixToDecompose[j][j]);
                for (int i = j + 1; i < n; i++) {
                    matrixToDecompose[i][j] *= dum;
                }
            }
        }

        //Imprime os valores da matriz triangular superior se necessário
        //formatUpperMatrix(upperTriangle);
        //Imprime os valores da matriz triangular inferior se necessário
        //formatLowerMatrix(lowerTriangle);

        return d;
    }

    //Realiza o método de substituição para trás
    public static void backwardsSubstitution(double[][] a, int n, double[] b) {
        int i, ii = 0, ip, j;
        double sum;

        for (i = 1; i <= n; i++) {
            ip = i - 1;
            sum = b[ip];
            b[ip] = b[i - 1];

            if (ii != 0) {
                for (j = ii; j <= i - 1; j++) {
                    sum -= a[i - 1][j - 1] * b[j - 1];
                }
            } else if (sum != 0) {
                ii = i;
            }

            b[i - 1] = sum;
        }
        for (i = n - 1; i >= 0; i--) {
            sum = b[i];
            for (j = i + 1; j < n; j++) {
                sum -= a[i][j] * b[j];
            }
            b[i] = sum / a[i][i];
        }
    }

    // Método para copiar uma matrix criando uma nova
    public static double[][] copyMatrix(double[][] originalMatrix, int size) {
        double[][] newMatrix = new double[size][originalMatrix[0].length];

        for (int line = 0; line < size; line++) {
            for (int column = 0; column < originalMatrix[line].length; column++) {
                newMatrix[line][column] = originalMatrix[line][column];
            }
        }

        return newMatrix;
    }

    public static void printMatrix(double[][] matrix) {

        for (int i = 0; i < matrix.length; i++) {
            for (int k = 0; k < matrix[i].length; k++) {
                System.out.printf("%10f ", matrix[i][k]);
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void formatUpperMatrix(double[][] upperTriangle) {

        for (int i = 0; i < upperTriangle.length; i++) {
            for (int j = 0; j < i; j++) {
                upperTriangle[i][j] = 0;
            }
        }
        System.out.println("U --->");
        printMatrix(upperTriangle);
    }

    public static void formatLowerMatrix(double[][] lowerTriangle) {

        for (int i = 0; i < lowerTriangle.length; i++) {
            for (int j = lowerTriangle[i].length - 1; j >= i; j--) {
                if (i == j) {
                    lowerTriangle[i][j] = 1;
                } else if (i < j) {
                    lowerTriangle[i][j] = 0;
                }
            }
        }
        System.out.println("L --->");
        printMatrix(lowerTriangle);
    }
}
