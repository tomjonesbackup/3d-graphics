package com.twj.gfx;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class MainWindow extends Frame {

    private GfxView gfxView;

    public MainWindow(World world) {
        super("3d Demo");
        setSize(620, 480);
        setLayout(new GridLayout());

        try {
            gfxView = new GfxView(world);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        add(gfxView);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                setVisible(false);
                dispose();
                System.exit(0);
            }
        });
    }
}
