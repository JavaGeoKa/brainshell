package main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class CardController {

    //ControllerSettings
    static int y = 591;
    static int[] positions = {149, 220, 292, 364, 436};
    static int cardPlace = 0;


    //card collection
    static int countFiles =0;
    private static final String DIR_PATH_RED = "/home/g/IdeaProjects/CardRecognizer/cards/red";
    private static final String DIR_PATH_BLACK = "/home/g/IdeaProjects/CardRecognizer/cards/black";

    //input file
//    private static final String PATH = "/home/g/Downloads/java_test_task/imgs/20180821_094458.109_0x1FE201D8.png";
    private static final String DIR_PATH = "/home/g/Downloads/java_test_task/imgs/";
    private static List<String> patchFiles = new ArrayList<>();


    static Map<BufferedImage, String> currentCards = new LinkedHashMap<>();
    static Map<BufferedImage, String> redCards = new HashMap<>();
    static Map<BufferedImage, String> blackCards = new HashMap<>();
    static TreeMap<Double, String> differences = new TreeMap<>();

    static BufferedImage img;
    static BufferedImage subImg;
    static StringBuilder answer = new StringBuilder();



    public static void main(String[] args) throws IOException {

        //загружаем коллекцию красных карт
        Files.walk(Paths.get(DIR_PATH_RED))
                .filter(Files::isRegularFile)
                .forEach(i -> {
                    try {
                        redCards.put(ImageIO.read(new File(i.toString())), i.getFileName().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        System.out.println("Red cards loaded: " + redCards.size());

        //загружаем коллекцию черных карт
        Files.walk(Paths.get(DIR_PATH_BLACK))
                .filter(Files::isRegularFile)
                .forEach(i -> {
                    try {
                        blackCards.put(ImageIO.read(new File(i.toString())), i.getFileName().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        System.out.println("Black cards loaded: " + blackCards.size());

        //Загружаем ссылки на скриншоты
        Files.walk(Paths.get(DIR_PATH))
                .filter(Files::isRegularFile)
                .forEach(i -> {
                    patchFiles.add(i.toString());
                    countFiles++;
                });
        System.out.println("Files to handle: " + countFiles);


        patchFiles.forEach(p -> {
            try {
                System.out.println(linkHandle(p));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }


//    обработка одного файла
    private static String linkHandle(String p) throws IOException {

        answer.setLength(0);
        //загрyжаем скриншот экрана
        img = ImageIO.read(new File(p));

        //вырезаю карты
        for (cardPlace = 0; cardPlace < 5; cardPlace++) {
            subImg = img.getSubimage(positions[cardPlace], y,29, 45);
            if (checkIsItCard(subImg)) {
                currentCards.put(subImg, checkColor(subImg));
            }
        }
//        cardPlace = 0;

        //Здесь нужно начать сравнивать два изображения
        currentCards.entrySet().stream().forEach( e -> {
            if (e.getValue() == "black") {
                //black compare
                answer.append(blackCompare(e.getKey()));
            } else if (e.getValue() == "red") {
                //red compare возвращает название карты с минимальной разницей
                answer.append(redCompare(e.getKey()));
            }
        });


        return p + " -> " + answer;
    }

    //сравниваю изображение с черными картами
    private static String blackCompare(BufferedImage key) {
        differences.clear();
        //добавить разницу в мепу
        blackCards.entrySet().stream().forEach(bc -> {
            try {
                differences.put(CardComparator.compare(bc.getKey(),key), bc.getValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        //возвращаем название карты
        return differences.firstEntry().getValue();

    }


    //сравниваю изображение с красными картами
    private static String redCompare(BufferedImage key) {
        differences.clear();
        //добавить разницу в мепу
        redCards.entrySet().stream().forEach(rc -> {
            try {
                differences.put(CardComparator.compare(rc.getKey(),key), rc.getValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        //возвращаем название карты
        return differences.firstEntry().getValue();

    }

    //определение цвета карты
    private static String checkColor(BufferedImage i) {
        if (i.getRGB(10,35) == -14474458) {
            return "black";
        } else if (i.getRGB(10,35) == -3323575) {
            return "red";
        }

        return "red";
    }

    //проверка карта или нет
    private static boolean checkIsItCard(BufferedImage i) {
        int backgroundQuantities = 0;
        for (int l = 0; l < i.getWidth(); l++) {
            if (i.getRGB(l, 1) == -1 ) {
                return true;
            } else if (i.getRGB(l, 1) == -8882056) {
                    backgroundChanger(i);
                    return true;
                }
            }
        return false;
    }

    //убираю тень
    private static void backgroundChanger(BufferedImage i) {
        for (int k = 0; k < i.getHeight(); k++) {
            for (int l = 0; l < i.getWidth(); l++) {
                if (i.getRGB(l, k) == -8882056) {
                    i.setRGB(l,k,-1);
                } else if (i.getRGB(l,k) == -10477022) {
                    i.setRGB(l, k, -3323575);
                } else if (i.getRGB(l,k) == -15724526){
                    i.setRGB(l, k, -14474458);
                }

            }
        }
    }


}
