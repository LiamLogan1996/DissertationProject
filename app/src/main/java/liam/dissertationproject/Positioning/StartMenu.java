/*
 * Created by Liam Logan
 * Copyright (c) 2018. All Rights reserved
 *
 * Last Modified .12/04/18 13:50
 */

package liam.dissertationproject.Positioning;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.GridLayout;

public class StartMenu extends Activity {

    GridLayout mainMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_menu);

        mainMenu = (GridLayout) findViewById(R.id.menuGrid);
        //set Event
        setSingleEvent(mainMenu);


    }

    private void setSingleEvent(GridLayout mainMenu) {
        for (int i = 0; i < mainMenu.getChildCount(); i++) {
            final CardView cardView = (CardView) mainMenu.getChildAt(i);
            final int finalI = i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (finalI == 0) {

                        Intent intent = new Intent(StartMenu.this, MainActivity.class);
                        startActivity(intent);
                    } else if (finalI == 1) {
                        Intent intent = new Intent(StartMenu.this, Preferences.class);
                        startActivity(intent);
                    } else if (finalI == 2) {
                        Intent timetable = new Intent(Intent.ACTION_VIEW, Uri.parse("https://portal.stir.ac.uk/security/login.jsp"));
                        startActivity(timetable);
                    } else if (finalI == 3) {
                        System.exit(0);

                    }
                }
            });
        }
    }
}