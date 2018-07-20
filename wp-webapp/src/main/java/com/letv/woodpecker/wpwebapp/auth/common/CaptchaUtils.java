package com.letv.woodpecker.wpwebapp.auth.common;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.geom.QuadCurve2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

/**
 * 验证码工具类
 * @author meijunjie 2018/7/3
 */
public class CaptchaUtils {

    private static final int WIDTH = 108, HEIGHT = 40, CODE_SIZE = 4;
    private static final String GENERATE_CHARS = "3456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final char[] charArray = GENERATE_CHARS.toCharArray();

    private static final Font[] RANDOM_FONT = new Font[]{
            new Font(Font.DIALOG, Font.BOLD, 33),
            new Font(Font.DIALOG_INPUT, Font.BOLD, 34),
            new Font(Font.SERIF, Font.BOLD, 33),
            new Font(Font.SANS_SERIF, Font.BOLD,34),
            new Font(Font.MONOSPACED, Font.BOLD, 34)
    };

    private static final Random RANDOM = new Random();

    static void generate(HttpServletResponse response, String viewCode){
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        response.setHeader("Pragma","no-cache");
        response.setHeader("Cache-control","no-cache");
        response.setDateHeader("Expires",0);
        response.setContentType("image/jpeg");

        ServletOutputStream sos = null;
        try{
            drawGraphic(image, viewCode);
            sos = response.getOutputStream();
            ImageIO.write(image,"JPEG", sos);
            sos.flush();
        } catch (Exception e) {
            throw new RuntimeException();
        } finally {
            // 关闭输出流
            if(sos != null){
                try{
                    sos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sos = null;
            }
        }
    }

    /**
     * 生成验证码字符串
     * @return        验证码字符串
     */
     static String generateCode(){
        int count = CODE_SIZE;
        char[] buffer = new char[count];
        for (int i = 0; i < count; i++) {
            buffer[i] = charArray[RANDOM.nextInt(charArray.length)];
        }
        return new String(buffer);
    }


     static void drawGraphic(BufferedImage image, String code)
    {
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        // 图形抗锯齿
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 字体抗锯齿
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        // 设定背景色，淡色
        graphics2D.setColor(getRandColor(210, 250));
        graphics2D.fillRect(0, 0, WIDTH, HEIGHT);

        // 画小字符背景
        Color color = null;
        for(int i = 0; i < 20; i++)
        {
            color = getRandColor(120, 200);
            graphics2D.setColor(color);
            String rand = String.valueOf(charArray[RANDOM.nextInt(charArray.length)]);
            graphics2D.drawString(rand, RANDOM.nextInt(WIDTH), RANDOM.nextInt(HEIGHT));
            color = null;
        }
        // 取随机产生的认证码(4位数字)
        char[] buffer = code.toCharArray();
        for (int i = 0; i < buffer.length; i++)
        {
            char code_ = buffer[i];
            //旋转度数 最好小于45度
            int degree = RANDOM.nextInt(28);
            if (i % 2 == 0) {
                degree = degree * (-1);
            }
            //定义坐标
            int x = 22 * i, y = 21;
            //旋转区域
            graphics2D.rotate(Math.toRadians(degree), x, y);
            //设定字体颜色
            color = getRandColor(20, 130);
            graphics2D.setColor(color);
            //设定字体，每次随机
            graphics2D.setFont(RANDOM_FONT[RANDOM.nextInt(RANDOM_FONT.length)]);
            //将认证码显示到图象中
            graphics2D.drawString("" + code_, x + 8, y + 10);
            //旋转之后，必须旋转回来
            graphics2D.rotate(-Math.toRadians(degree), x, y);
        }
        //图片中间曲线，使用上面缓存的color
        graphics2D.setColor(color);
        //width是线宽,float型
        BasicStroke bs = new BasicStroke(3);
        graphics2D.setStroke(bs);
        //画出曲线
        QuadCurve2D.Double curve = new QuadCurve2D.Double(0d, RANDOM.nextInt(HEIGHT - 8) + 4, WIDTH / 2, HEIGHT / 2, WIDTH, RANDOM.nextInt(HEIGHT - 8) + 4);
        graphics2D.draw(curve);
        // 销毁图像
        graphics2D.dispose();
    }


    /**
     * 给定范围随机生成颜色
     */
    private static Color getRandColor(int fc, int bc) {
            if (fc > 255)
                fc = 255;
            if (bc > 255)
                bc = 255;
            int r = fc + RANDOM.nextInt(bc - fc);
            int g = fc + RANDOM.nextInt(bc - fc);
            int b = fc + RANDOM.nextInt(bc - fc);
            return new Color(r, g, b);
        }

}
