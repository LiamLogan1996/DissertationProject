/*
 * Created by Liam Logan
 * Copyright (c) 2018. All Rights reserved
 *
 *
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

        // sets layout for activity
        setContentView(R.layout.activity_start_menu);

        // Makes reference to the GridMenu Options
        mainMenu = (GridLayout) findViewById(R.id.menuGrid);
        //set Event
        setSingleEvent(mainMenu);


    }

    /**
     * GridLayout Menu which appears at beginning of application.
     * @param mainMenu menu object
     */
    private void setSingleEvent(GridLayout mainMenu) {

        // For loop to cycle round the grid options. Each grid button has an index.
        for (int i = 0; i < mainMenu.getChildCount(); i++) {
            final CardView cardView = (CardView) mainMenu.getChildAt(i);
            final int finalI = i;
            cardView.setOnClickListener(new View.OnClickListener() {

                /**
                 * This will display the menu options available. A index 0 is the first menu option,
                 * Index 1 is the second and so on.
                 * @param view view to be displayed
                 */
                @Override
                public void onClick(View view) {
                    if (finalI == 0) {
                        Intent intent = new Intent(StartMenu.this, MainActivity.class);
                        startActivity(intent);
                    } else if (finalI == 1) {
                        Intent intent = new Intent(StartMenu.this, Preferences.class);
                        startActivity(intent);
                    } else if (finalI == 2) {

                        // Takes user to portal login to access timetable.
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