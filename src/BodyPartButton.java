import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.image.RescaleOp;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * '누끼' 감지 및 자동 호버 이미지 생성 버튼 (포커스 테두리 제거)
 */
public class BodyPartButton extends JButton {

    private BufferedImage originalImage;
    private BufferedImage hoverImage;

    public BodyPartButton(String fileName, float[] hoverTint) {
        
        try {
            File imgFile = new File("img",fileName);
            System.out.println("이미지 절대 경로 = " + imgFile.getAbsolutePath());
            System.out.println("존재하나? " + imgFile.exists());

            originalImage = ImageIO.read(imgFile);   // ← 파일에서 바로 읽기
            hoverImage = createHoverImage(originalImage, hoverTint);

            setIcon(new ImageIcon(originalImage));
            setRolloverIcon(new ImageIcon(hoverImage));
            setPressedIcon(new ImageIcon(hoverImage));

            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);

            Dimension size = new Dimension(
                originalImage.getWidth(),
                originalImage.getHeight()
            );
            setPreferredSize(size);

        } catch (IOException e) {
            e.printStackTrace();
            setText("이미지 로드 실패");
        }
    }
    

    private BufferedImage createHoverImage(BufferedImage image, float[] scales) {
        if (image == null) return null;
        BufferedImage newImage = new BufferedImage(
                image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        RescaleOp op = new RescaleOp(scales, new float[4], null);
        newImage.createGraphics().drawImage(image, op, 0, 0);
        return newImage;
    }

    @Override
    public boolean contains(int x, int y) {
        if (originalImage == null || x < 0 || x >= originalImage.getWidth() || y < 0 || y >= originalImage.getHeight()) {
            return false;
        }
        int alpha = (originalImage.getRGB(x, y) >> 24) & 0xFF;
        return alpha > 50;
    }
}