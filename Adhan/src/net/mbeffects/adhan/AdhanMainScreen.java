package net.mbeffects.adhan;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.system.Application;
import java.util.*;

public class AdhanMainScreen extends MainScreen {

    // Translated to Arabic
    private static final String[] NAMES =
        { "الفجر", "الشروق", "الظهر", "العصر", "المغرب", "العشاء" };

    private LabelField[] timeLbls = new LabelField[6];
    private LabelField   lblNext;
    private LabelField   lblDate;
    private TimerService _timer;
    
    private Font fontHeader;
    private Font fontSub;
    private Font fontRowName;
    private Font fontRowTime;

    public AdhanMainScreen() {
        super(MainScreen.VERTICAL_SCROLL | MainScreen.VERTICAL_SCROLLBAR);
        
        // 1. Set the main background to White
        getMainManager().setBackground(BackgroundFactory.createSolidBackground(Color.WHITE));
        
        // IMPORTANT: Initialize fonts BEFORE using them in the title label
        initFonts();

        // 2. Create the custom title label
        CustomColorLabel titleLabel = new CustomColorLabel(
            "أوقات الصلاة لمدينة وادي زم", 
            0x00222222, // Dark gray/black color
            fontHeader, // Reusing your large header font
            DrawStyle.HCENTER | Field.FIELD_HCENTER | Field.USE_ALL_WIDTH
        );

        // 3. Force the title background to be white as well
        titleLabel.setBackground(BackgroundFactory.createSolidBackground(Color.WHITE));
        titleLabel.setPadding(10, 0, 10, 0);

        // Set this custom label as the screen's title
        setTitle(titleLabel); 
        
        try {
            buildUI();
        } catch (Throwable t) {
            add(new LabelField("UI Error: " + t.getMessage()));
            return;
        }
        
        try {
            refresh();
        } catch (Throwable t) {
            lblNext.setText("Calc error: " + t.getMessage());
        }
        
        _timer = new TimerService(this);
        _timer.start();
    }

    private void initFonts() {
        try {
            Font base = Font.getDefault();
            fontHeader = base.derive(Font.BOLD, 30);
            fontSub = base.derive(Font.PLAIN, 20);
            fontRowName = base.derive(Font.BOLD, 28); 
            fontRowTime = base.derive(Font.BOLD, 26);
        } catch (Exception e) {
            fontHeader = Font.getDefault();
            fontRowTime = Font.getDefault();
        }
    }

    private void buildUI() {
        VerticalFieldManager header = new VerticalFieldManager(USE_ALL_WIDTH | FIELD_HCENTER);
        header.setPadding(20, 15, 20, 15);
        
        // 4. Change header shade to White
        header.setBackground(BackgroundFactory.createSolidBackground(Color.WHITE)); 

        lblNext = new CustomColorLabel("", 0x002E7D32, fontHeader, FIELD_HCENTER); 
        lblDate = new CustomColorLabel("", 0x00777777, fontSub, FIELD_HCENTER); 

        header.add(lblNext);
        header.add(lblDate);
        add(header);

        for (int i = 0; i < 6; i++) {
            PrayerRowManager row = new PrayerRowManager();
            
            LabelField name = new CustomColorLabel(NAMES[i], 0x00444444, fontRowName, NON_FOCUSABLE);
            timeLbls[i] = new CustomColorLabel("--:--", Color.BLACK, fontRowTime, NON_FOCUSABLE);
            
            row.add(name);
            row.add(timeLbls[i]);
            add(row);
        }
    }

    public void refresh() {
        Date now   = new Date();
        String[] t = PrayerCalculator.calculate(now);
        for (int i = 0; i < 6; i++) {
            timeLbls[i].setText(t[i]);
        }

        Calendar c = Calendar.getInstance();
        c.setTime(now);
        lblDate.setText(c.get(Calendar.DAY_OF_MONTH) + " / "
            + (c.get(Calendar.MONTH) + 1) + " / " + c.get(Calendar.YEAR));

        long nowMs = now.getTime();
        int  next  = -1;
        for (int i = 0; i < 6; i++) {
            if (i == 1) continue; 
            int ph = Integer.parseInt(t[i].substring(0,2));
            int pm = Integer.parseInt(t[i].substring(3,5));
            Calendar p = Calendar.getInstance();
            p.setTime(now);
            p.set(Calendar.HOUR_OF_DAY, ph);
            p.set(Calendar.MINUTE,      pm);
            p.set(Calendar.SECOND,      0);
            p.set(Calendar.MILLISECOND, 0);
            if (p.getTime().getTime() > nowMs) { next = i; break; }
        }
        
        lblNext.setText(next >= 0
            ? "الصلاة القادمة: " + NAMES[next] + " في " + t[next]
            : "انتهت صلوات اليوم");
    }

    public boolean onClose() {
        Application.getApplication().requestBackground();
        return false;
    }

    // --- The Magic Happens Here ---
    private class PrayerRowManager extends Manager {
        public PrayerRowManager() {
            super(USE_ALL_WIDTH);
        }

        protected void sublayout(int width, int height) {
            if (getFieldCount() < 2) return;
            
            Field name = getField(0); // This is the Arabic Name
            Field time = getField(1); // This is the Time
            
            layoutChild(name, width, height);
            layoutChild(time, width, height);

            int paddingY = 15;
            int paddingX = 20;

            // THE TRICK: Reverse the X coordinates
            
            // 1. Put the Time on the far LEFT
            setPositionChild(time, paddingX, paddingY);
            
            // 2. Put the Arabic Name on the far RIGHT
            setPositionChild(name, width - name.getWidth() - paddingX, paddingY);

            setExtent(width, Math.max(name.getHeight(), time.getHeight()) + (paddingY * 2));
        }

        protected void paint(Graphics graphics) {
            super.paint(graphics);
            graphics.setColor(0x00EAEAEA); 
            graphics.drawLine(20, getHeight() - 1, getWidth() - 20, getHeight() - 1);
        }
    }

    private class CustomColorLabel extends LabelField {
        private int color;

        public CustomColorLabel(String text, int color, Font font, long style) {
            super(text, style);
            this.color = color;
            setFont(font);
        }

        protected void paint(Graphics graphics) {
            graphics.setColor(color);
            super.paint(graphics);
        }
    }
}