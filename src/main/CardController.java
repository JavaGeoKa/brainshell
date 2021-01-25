package main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class CardController {

    static int y = 591;
    static int[] positions = {149, 220, 292, 364, 436};
    static int cardPlace = 0;
    static int countFiles =0;

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
        Files.walk(Paths.get(new File("").getAbsolutePath()+"/cards/red/"))
                .filter(Files::isRegularFile)
                .forEach(i -> {
                    try {
                        redCards.put(ImageIO.read(new File(i.toString())), i.getFileName().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        Files.walk(Paths.get(new File("").getAbsolutePath()+"/cards/black/"))
                .filter(Files::isRegularFile)
                .forEach(i -> {
                    try {
                        blackCards.put(ImageIO.read(new File(i.toString())), i.getFileName().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        Files.walk(Paths.get(args[0]))
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
    private static String linkHandle(String p) throws IOException {
        img = ImageIO.read(new File(p));
        answer.setLength(0);
        currentCards.clear();
        for (cardPlace = 0; cardPlace < 5; cardPlace++) {
            subImg = img.getSubimage(positions[cardPlace], y,29, 45);
            if (checkIsItCard(subImg)) {
                currentCards.put(subImg, checkColor(subImg));
            }
        }
        currentCards.entrySet().stream().forEach( e -> {
            if (e.getValue() == "black") {
                answer.append(blackCompare(e.getKey()));
            } else if (e.getValue() == "red") {
                answer.append(redCompare(e.getKey()));
            }
        });
        currentCards.clear();
        return p + " -> " + answer;
    }

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

    private static String redCompare(BufferedImage key) {
        differences.clear();
        redCards.entrySet().stream().forEach(rc -> {
            try {
                differences.put(CardComparator.compare(rc.getKey(),key), rc.getValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return differences.firstEntry().getValue();

    }

    private static String checkColor(BufferedImage i) {
        if (i.getRGB(10,35) == -14474458) {
            return "black";
        } else if (i.getRGB(10,35) == -3323575) {
            return "red";
        }
        return "red";
    }

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
