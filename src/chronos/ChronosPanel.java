/*
 * Copyright MalchroSoft - Aymeric MALCHROWICZ. All right reserved.
 * The source code that contains this comment is an intellectual property
 * of MalchroSoft [Aymeric MALCHROWICZ]. Use is subject to licence terms.
 */

/*
 * ChronosPanel.java
 *
 * Created on 23 juil. 2010, 01:14:41
 */
package chronos;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTargetAdapter;
import org.jdesktop.swingx.JXPanel;

/**
 *
 * @author Aymeric Malchrowicz
 */
public class ChronosPanel extends JXPanel
{
    private final SimpleDateFormat fmt;
    private Thread chronoThread;
    private Date dateToReach;
    private boolean running;
    private JFrame parent;

    /** Creates new form ChronosPanel */
    public ChronosPanel(JFrame parent)
    {
        this.parent = parent;
        this.fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        initComponents();
        this.setPreferredSize(new Dimension(350, 200));

        this.dateText.setText(this.fmt.format(
            new Date(new Date().getTime() + (2l * 3600l * 1000l))));
        this.running = false;

        this.startButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (startButton.getText().toUpperCase().equals("START"))
                {
                    startAction();
                }
                else
                {
                    stopAction();
                }
            }
        });


    }

    private void startAction()
    {
        this.dateToReach = null;
        try
        {
            dateToReach = this.fmt.parse(this.dateText.getText());
        } catch (ParseException ex)
        {
            dateToReach = new Date(new Date().getTime() + 2l * 24l * 3600 * 1000);
            this.dateText.setText(this.fmt.format(dateToReach));
        }
        startButton.setText("STOP");
        startButton.setForeground(new Color(255, 0, 0));
        this.chronoThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                int hours = 0;
                int minutes = 0;
                int secs = 0;
                int days = 0;
                int months = 0;
                int year = 0;
                long startTime = ((System.currentTimeMillis() - dateToReach.getTime()) / 1000l);
                if (startTime < 0)
                {
                    startTime = -startTime;
                }
                long time = startTime;
                String rest;
                while (running)
                {
                    rest = "";
                    time = ((System.currentTimeMillis() - dateToReach.getTime()) / 1000l);
                    if (time < 0)
                    {
                        time = -time;
                    }
                    if (time < 1)
                    {
                        stopAction();
                        try
                        {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex)
                        {
                            Logger.getLogger(ChronosPanel.class.getName()).log(Level.SEVERE, null,
                                ex);
                        }
                        goToIn(new Point((int) parent.getLocation().getX(),
                            (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight()), 500, 3);
                    }
                    else if (time % 3600 == 0)
                    {
                        goToIn(new Point((int) parent.getLocation().getX(),
                            (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight()), 300, 1);
                    }
                    else if (time < 5)
                    {
                        vibroInThread((int) (20 - time));
                    }
                    else if (time < 60)
                    {
                        vibroInThread(10);
                    }
                    else if (time < 3600)
                    {
                        glingItInThread(0);
                    }
                    else if (time < 3600 * 5)
                    {
                        glingItInThread((int) ((time) / 4000));
                    }
                    else if (time < 3600 * 24)
                    {
                        glingItInThread((int) ((time) / 5000));
                    }
                    else if (time < 3600 * 48)
                    {
                        glingItInThread(secs * 2);
                    }
                    else /*stay stable*/;
                    pb.setValue((int) (time * 100 / startTime));

                    year = (int) (time / (365.25 * 24 * 3600));
                    if (year >= 2)
                    {
                        rest += year + "ans ";
                        time -= (year * (365.25 * 24 * 3600));
                    }
                    else if (year >= 1)
                    {
                        rest += year + "an ";
                        time -= (year * (365.25 * 24 * 3600));
                    }
                    months = (int) (time / (30 * 24 * 3600));
                    if (months >= 1)
                    {
                        rest += months + "mois ";
                        time -= (months * (30 * 24 * 3600));
                    }
                    days = (int) (time / (24 * 3600));
                    if (days >= 1)
                    {
                        rest += days + "j ";
                        time -= (days * (24 * 3600));
                    }
                    hours = (int) (time / 3600);
                    rest += hours + ":";
                    time -= (hours * 3600);
                    minutes = (int) (time / 60);
                    rest += minutes + ":";
                    time -= (minutes * 60);
                    secs = (int) time;
                    rest += secs + "s";

                    pb.repaint();
                    ChronosPanel.this.timeLabel.setText(rest);
                    try
                    {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex)
                    {
                    }
                    parent.setTitle(rest + " - MalchroSoft Chronos");
                }
            }
        });

        this.chronoThread.start();
        this.running = true;
    }

    private void stopAction()
    {
        startButton.setText("START");
        startButton.setForeground(new Color(0, 255, 0));
        this.chronoThread.interrupt();
        this.chronoThread = null;
        this.running = false;
    }

    public void glingItInThread(final int resistance)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                glingIt(resistance);
            }
        }).start();
    }

    public synchronized void vibroInThread(final int n)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                treatVibro(n);
            }
        }).start();
    }

    private void glingIt(int resitance)
    {
        Point p = this.parent.getLocation();
        if (resitance < 1)
        {
            resitance = 1;
        }
        int move = (20 / resitance);
        for (int i = 0; i < 20; i++)
        {
            this.parent.setLocation((int) p.getX() + move, (int) p.getY() + move);
            this.parent.repaint();
            try
            {
                Thread.sleep(50);
            } catch (InterruptedException ex)
            {
            }
            move = -move / 2;
        }
        this.parent.setLocation(p);
    }

    private void treatVibro(int n)
    {
        Rectangle originalBound = this.parent.getBounds();
        Rectangle bound = this.parent.getBounds();
        for (int i = 0; i < n; i++)
        {
            try
            {
                Thread.sleep(50);
            } catch (InterruptedException ex)
            {
            }
            if (i % 2 == 0)
            {
                bound.setLocation(
                    Math.round((float) bound.getX()) + 10,
                    Math.round((float) bound.getY()) + 5);
            }
            else
            {
                bound.setLocation(
                    Math.round((float) bound.getX()) - 10,
                    Math.round((float) bound.getY()) - 5);
            }
            this.parent.setBounds(bound);
            this.parent.repaint();
        }
        this.parent.setBounds(originalBound);
    }

    private synchronized void goToIn(final Point p, int sec, int rebonds)
    {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        if ((p.getX() + this.getWidth()) > d.getWidth())
        {
            p.setLocation(d.getWidth() - this.getWidth(), p.getY());
        }
        if ((p.getY() + this.getHeight()) > d.getHeight())
        {
            p.setLocation(p.getX(), d.getHeight() - this.getHeight());
        }
        final Point cp = new Point();
        final Point origP = this.parent.getLocation();
        Animator a = new Animator(sec, rebonds * 2, Animator.RepeatBehavior.REVERSE, new TimingTargetAdapter()
        {
            @Override
            public void timingEvent(float fraction)
            {
                cp.setLocation(Math.min(p.getX(), origP.getX()) + ((Math.max(p.getX(), origP.getX()) - Math.min(
                    p.getX(), origP.getX())) * fraction),
                    Math.min(p.getY(), origP.getY()) + ((Math.max(p.getY(), origP.getY()) - Math.min(
                    p.getY(), origP.getY())) * fraction));
                parent.setLocation((int) cp.getX(), (int) cp.getY());
            }

            @Override
            public void begin()
            {
                //
            }

            @Override
            public void end()
            {
//                parent.setLocation(p);
            }
        });
        a.setAcceleration(0.2f);
//        a.setDeceleration(0.3f);
        a.setStartDirection(Animator.Direction.FORWARD);
        a.setStartFraction(0);
        a.start();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        pb = new javax.swing.JProgressBar();
        startButton = new javax.swing.JButton();
        timeLabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        dateText = new javax.swing.JFormattedTextField();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        jPanel2.setMaximumSize(new java.awt.Dimension(32000, 25));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        pb.setMaximumSize(new java.awt.Dimension(32767, 20));
        jPanel2.add(pb);

        jPanel1.add(jPanel2);

        startButton.setText("Start");
        startButton.setToolTipText("Mise en route");
        jPanel1.add(startButton);

        add(jPanel1, java.awt.BorderLayout.PAGE_END);

        timeLabel.setBackground(new java.awt.Color(255, 255, 255));
        timeLabel.setFont(new java.awt.Font("Mandela", 1, 36)); // NOI18N
        timeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        timeLabel.setText("0s");
        timeLabel.setOpaque(true);
        add(timeLabel, java.awt.BorderLayout.CENTER);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jLabel1.setText(" Date :");
        jPanel3.add(jLabel1, java.awt.BorderLayout.LINE_START);

        dateText.setForeground(new java.awt.Color(102, 102, 102));
        dateText.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm"))));
        dateText.setText("31/07/2010 00:00");
        dateText.setToolTipText("Date à atteindre");
        dateText.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        dateText.setFont(new java.awt.Font("Lucida Console", 1, 14)); // NOI18N
        jPanel3.add(dateText, java.awt.BorderLayout.CENTER);

        add(jPanel3, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFormattedTextField dateText;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JProgressBar pb;
    private javax.swing.JButton startButton;
    private javax.swing.JLabel timeLabel;
    // End of variables declaration//GEN-END:variables
}
