package com.github.kode_zon.jwt_clock;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Calendar;
import javax.swing.*;

public class MainApplication extends JDialog {

    public static void main(String[] args) {
        new MainApplication(new JFrame(), "Clock");
    }

    public MainApplication(JFrame frame, String str) {

        super(frame, str);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                System.exit(0);
            }
        });
        MainApplication self = this;

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException ex) {
                } catch (InstantiationException ex) {
                } catch (IllegalAccessException ex) {
                } catch (UnsupportedLookAndFeelException ex) {
                }

                JPanel panel = new JPanel();
                self.setSize(180, 80);
                self.setAlwaysOnTop (true);
                self.setVisible(true);

                int mouseX = MouseInfo.getPointerInfo().getLocation().x; // Mouse X Coordinate
                int mouseY = MouseInfo.getPointerInfo().getLocation().y; // Mouse Y Coordinate

                int frameX = self.getX(); // Mouse X Coordinate
                int frameY = self.getY(); // Mouse Y Coordinate

                int diffX = Math.abs(mouseX - frameX); // The distance from JFrame X Coordinate to Mouse X Coordinate.
                int diffY = Math.abs(mouseY - frameY); // The distance from JFrame Y Coordinate to Mouse Y Coordinate.


//                System.out.println(String.format("Mouse (x:%d, y:%d)", mouseX, mouseY));
//                System.out.println(String.format("Frame (x:%d, y:%d)", frameX, frameY));

                if (diffY > 45 || diffX > 45) { // Y Coordinate 0 - 45 is the height of the top part of the window. Where you can move the frame.
                    self.setLocation(mouseX, mouseY);
                }



                Container content = self.getContentPane();
                content.setBackground(Color.white);
                content.setLayout(new FlowLayout());
                final JLabel textLabel = new JLabel("Clock");
                Font f = textLabel.getFont();
                Font newFont = new Font(f.getFontName(), f.getStyle(), 32);
                textLabel.setFont(newFont);
                content.add(textLabel);

                Timer timer = new Timer(500, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Calendar cal = Calendar.getInstance();
                        String template1 = "%02d:%02d:%02d";
                        String template2 = "%02d %02d %02d";
                        boolean tiktok = (cal.get(Calendar.MILLISECOND) > 500);

                        String hourStr = String.format(((tiktok)?template1:template2),  cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));

                        textLabel.setText(hourStr);
                    }
                });
                timer.setRepeats(true);
                timer.setCoalesce(true);
                timer.start();
            }

        });
    }

    public class TestPane extends JPanel {

        private DigitPane hour;
        private DigitPane min;
        private DigitPane second;
        private JLabel[] seperator;

        private int tick = 0;

        public TestPane() {

            setLayout(new GridBagLayout());

            hour = new DigitPane();
            min = new DigitPane();
            second = new DigitPane();
            seperator = new JLabel[]{new JLabel(":"), new JLabel(":")};

            add(hour);
            add(seperator[0]);
            add(min);
            add(seperator[1]);
            add(second);

            Timer timer = new Timer(500, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Calendar cal = Calendar.getInstance();
                    hour.setValue(cal.get(Calendar.HOUR_OF_DAY));
                    min.setValue(cal.get(Calendar.MINUTE));
                    second.setValue(cal.get(Calendar.SECOND));

                    if (tick % 2 == 1) {
                        seperator[0].setText(" ");
                        seperator[1].setText(" ");
                    } else {
                        seperator[0].setText(":");
                        seperator[1].setText(":");
                    }
                    tick++;
                }
            });
            timer.setRepeats(true);
            timer.setCoalesce(true);
            timer.start();
        }

    }

    public class DigitPane extends JPanel {

        private int value;

        @Override
        public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(getFont());
            return new Dimension(fm.stringWidth("00"), fm.getHeight());
        }

        public void setValue(int aValue) {
            if (value != aValue) {
                int old = value;
                value = aValue;
                firePropertyChange("value", old, value);
                repaint();
            }
        }

        public int getValue() {
            return value;
        }

        protected String pad(int value) {
            StringBuilder sb = new StringBuilder(String.valueOf(value));
            while (sb.length() < 2) {
                sb.insert(0, "0");
            }
            return sb.toString();
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setFont(getFont());

            super.paintComponent(g);
            String text = pad(getValue());
            FontMetrics fm = getFontMetrics(g.getFont());
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = ((getHeight()- fm.getHeight()) / 2) + fm.getAscent();
            g.drawString(text, x, y);
        }
    }
}