//Name: Harkirpa Kaur
//Date: June 14, 2024
//Game: Infinity Sweeper

package com.example.basic;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    //grid dimensions
    int row = 15;
    int col = 11;
    //tracks if it's the user's first move
    boolean firstMove = true;
    //array to hold field (mines, powerups, neighbours)
    int field[][] = new int [row][col];
    //array to track if a square has been opened
    int showSquare[][] = new int [row][col];
    //array to track which tiles have been flagged
    int flags[][] = new int [row][col];
    //boolean to track if flag mode is on or not
    boolean flag = false;
    //array to hold pictures for the grid
    ImageView pics [] = new ImageView[row*col];
    //checks if user is using hammer
    boolean useHammer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //links grid
        GridLayout g = (GridLayout) findViewById(R.id.grid);
        int m = 0;
        //iterates through grid to set up tiles as imageViews
        for (int i = 0 ; i < row ; i++){
            for (int j = 0 ; j < col ; j++){
                pics[m] = new ImageView(this);
                setPicStart(pics[m], m);
                pics[m].setId(m);
                pics[m].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //calls gridButtonClick method when a tile is clicked, with the id of the tile clicked
                        gridButtonClick(v.getId());
                    }
                });
                g.addView(pics[m]);
                m++;
            }
        }
        //powerup buttons
        ImageButton widow = findViewById(R.id.widow);
        ImageButton hammer = findViewById(R.id.hammer);
        //adding mines
        addMines(6);
        //adding powerups
        //hammer
        addPowerups(11);
        //widow
        addPowerups(12);
        //skrull
        addPowerups(13);
        //disabling powerup buttons
        hammer.setEnabled(false);
        widow.setEnabled(false);
    }
    //sets up grid initially
    public void setPicStart(ImageView i, int pos){
        //x position of tile
        int x = pos/col;
        //y position of tile
        int y = pos%col;
        //covers all tiles initially
        pics[pos].setImageResource(R.drawable.cover);
    }
    public void gridButtonClick(int pos) {
        //powerup buttons
        ImageButton hammer = (ImageButton) findViewById(R.id.hammer);
        ImageButton widow = (ImageButton) findViewById(R.id.widow);
        //x position of tile clicked
        int x = pos / col;
        //y position of tile clicked
        int y = pos % col;
        //if this is the user's first move
        if (firstMove){
            //call empty (shows a part of the grid without revealing mines)
            empty(x,y);
            //end first move
            firstMove = false;
        }
        //if flag mode is not on
        else if (!flag) {
            //if user clicked on a mine (10)
            if (field[x][y] == 10) {
                //makes a dialog box telling the user that they lost
                makeDialogBox();
            }
            //if user is using a hammer
            if (useHammer){
                //show the tile they clicked on
                showSquare[x][y] = 1;
                //checks bounds and shows all surrounding tiles
                if (x + 1 < row)
                    showSquare[x + 1][y] = 1;
                if (x + 1 < row && y + 1 < col)
                    showSquare[x + 1][y + 1] = 1;
                if (x + 1 < row && y - 1 >= 0)
                    showSquare[x + 1][y - 1] = 1;
                if (x - 1 >= 0)
                    showSquare[x - 1][y] = 1;
                if (x - 1 >= 0 && y + 1 < col)
                    showSquare[x - 1][y + 1] = 1;
                if (x - 1 >= 0 && y - 1 >= 0)
                    showSquare[x - 1][y - 1] = 1;
                if (y + 1 < col)
                    showSquare[x][y + 1] = 1;
                if (y - 1 >= 0)
                    showSquare[x][y - 1] = 1;
                //set hammer to false
                useHammer = false;
                //disable hammer button
                hammer.setEnabled(false);
            }
            //if user clicks on a tile with a hammer (11)
            else if (showSquare[x][y] == 0 && field[x][y] == 11)
                //enable hammer button
                hammer.setEnabled(true);
            //if user clicks on a tile with a widow (12)
            else if (showSquare[x][y] == 0 && field [x][y] == 12)
                //enable widow button
                widow.setEnabled(true);
            //if user clicks on a button with a skrull
            else if (showSquare[x][y] == 0 && field[x][y] == 13)
                //call skrull method
                skrull();
            //if user clicks on an empty tile
            else if (field [x][y] == 0)
                //call empty with the coordinates of the tile clicked
                empty(x, y);
            //show square that the user clicked on
            showSquare[x][y] = 1;
        }
        //if flag mode is on
        else {
            //if theres no flag on the tile
            if (flags[x][y] == 0 && showSquare [x][y] == 0 || field[x][y] == 10)
                //add a flag
                flags[x][y] = 1;
                //is there is a flag on the tile
            else
                //remove flag
                flags[x][y] = 0;
        }
        redraw();
        //if the user won
        if(win()){
            //go to win screen (intent)
            Intent i = new Intent(this, win.class);
            startActivity(i);
        }
    }
        public void redraw() {
        //widow button
        ImageButton widow = findViewById(R.id.widow);
        //position of tile clicked in 1D array
        int m = 0;
        //set stone to 0
        int stone = 0;
        //iterate through grid
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                //if there is a flag
                if (flags[i][j] == 1)
                    //show a flag on the tile
                    pics[m].setImageResource(R.drawable.flag);
                //if the tile has not been clicked
                else if (showSquare[i][j] == 0 && flags[i][j] == 0)
                    //show a cover
                    pics[m].setImageResource(R.drawable.cover);
                //if the tile has been clicked and there is nothing (0)
                else if (showSquare[i][j] == 1 && field [i][j] == 0)
                    //show an empty square
                    pics[m].setImageResource(R.drawable.blank);
                //if the tiles has been clicked and there is no flag and there is a mine
                else if (showSquare[i][j] == 1 && flags[i][j] == 0 && field[i][j] == 10){
                    //if stone is zero
                    if (stone == 0)
                        //show time stone
                        pics[m].setImageResource(R.drawable.time);
                    else if (stone == 1)
                        //show power stone
                        pics[m].setImageResource(R.drawable.power);
                    else if (stone == 2)
                        //show space stone
                        pics[m].setImageResource(R.drawable.space);
                    else if (stone == 3)
                        //show mind stone
                        pics[m].setImageResource(R.drawable.mind);
                    else if (stone == 4)
                        //show reality stone
                        pics[m].setImageResource(R.drawable.reality);
                    else if (stone == 5)
                        //show soul stone
                        pics[m].setImageResource(R.drawable.soul);
                    //once all 5 stones have been shown
                    else if (stone > 5)
                        //show sensor
                        pics[m].setImageResource(R.drawable.sensor);
                    //add one to stone everytime to show different stones on every mine
                    stone++;
                }
                //if there is a fake stone (20)
                else if (showSquare[i][j] == 1 && flags[i][j] == 0 && field[i][j] == 20) {
                    //show a fake stone
                    System.out.println("226");
                    pics[m].setImageResource(R.drawable.fake);
                }
                //if there is a hammer (11)
                else if (showSquare[i][j] == 1 && flags[i][j] == 0 && field[i][j] == 11)
                    //show a hammer
                    pics[m].setImageResource(R.drawable.hammer);
                //if there is a widow (12)
                else if (showSquare[i][j] == 1 && flags[i][j] == 0 && field[i][j] == 12)
                    //show a widow
                    pics[m].setImageResource(R.drawable.widow);
                //if there is a skrull (13)
                else if (showSquare[i][j] == 1 && flags[i][j] == 0 && field[i][j] == 13)
                    //show a skrull
                    pics[m].setImageResource(R.drawable.skrull);
                //shows the number based on tile value in field array (1-8)
                else if (showSquare[i][j] == 1 && flags[i][j] == 0 && field[i][j] == 1)
                    pics[m].setImageResource(R.drawable.one);
                else if (showSquare[i][j] == 1 && flags[i][j] == 0 && field[i][j] == 2)
                    pics[m].setImageResource(R.drawable.two);
                else if (showSquare[i][j] == 1 && flags[i][j] == 0 && field[i][j] == 3)
                    pics[m].setImageResource(R.drawable.three);
                else if (showSquare[i][j] == 1 && flags[i][j] == 0 && field[i][j] == 4)
                    pics[m].setImageResource(R.drawable.four);
                else if (showSquare[i][j] == 1 && flags[i][j] == 0 && field[i][j] == 5)
                    pics[m].setImageResource(R.drawable.five);
                else if (showSquare[i][j] == 1 && flags[i][j] == 0 && field[i][j] == 6)
                    pics[m].setImageResource(R.drawable.six);
                else if (showSquare[i][j] == 1 && flags[i][j] == 0 && field[i][j] == 7)
                    pics[m].setImageResource(R.drawable.seven);
                else if (showSquare[i][j] == 1 && flags[i][j] == 0 && field[i][j] == 8)
                    pics[m].setImageResource(R.drawable.eight);
                m++;
            }
        }
    }

    public void empty(int x, int y) {
        // reveal clicked square
        showSquare[x][y] = 1;

        // Array to represent 8 possible directions: up, down, left, right, and diagonals
        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};

        // check all directions
        for (int i = 0; i < 8; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];

            // Check if the new coordinates are within the bounds of the grid
            if (newX >= 0 && newX < row && newY >= 0 && newY < col && showSquare[newX][newY] == 0) {
                // If the new square is empty, recursively call empty on it
                if (field[newX][newY] == 0) {
                    empty(newX, newY);
                }
                // If the new square has a non-zero value, but is not a mine or a power up, mark it as revealed and stop
                else if (field[newX][newY] != 0) {
                    if (field[newX][newY] != 10 && field[newX][newY] != 11 && field[newX][newY] != 12 && field[newX][newY] != 13 && field[newX][newY] != 20) {
                        showSquare[newX][newY] = 1;
                        break;
                    }
                }
            }
        }
    }
    //adds mines to grid
    public void addMines(int amt){
        //gets a random position 13 times
        for (int i = 0 ; i < 13 ; i ++){
            int x = (int)(Math.random()*row);
            int y = (int)(Math.random()*col);
            //keep picking a new position until the position picked is empty (0)
            while (field[x][y]!=0){
                x = (int)(Math.random()*row);
                y = (int)(Math.random()*col);
            }
            //put a mine at the position picked (10)
            field[x][y] = 10;
        }
        //call neighbours
        neighbours();
    }

    public void skrull (){
        //pick a random tile position on the grid
        int x = (int)(Math.random()*row);
        int y = (int)(Math.random()*col);
        //repick position until it is empty, not showing, and does not have a flag
        while (field[x][y] != 0 || showSquare[x][y] == 1 || flags[x][y] == 1){
            x = (int)(Math.random()*row);
            y = (int)(Math.random()*col);
        }
        //set position to hold a fake stone (20)
        field[x][y] = 20;
        System.out.println(x+", "+y);
        //call new neighbours to account of the newly added fake mine
        newNeighbours(x, y);
        redraw();
    }
    //adds powerups on the grid
    public void addPowerups (int num){
        //picks a random tile position
        int x = (int)(Math.random()*row);
        int y = (int)(Math.random()*col);
        //repicks position until it is empty
        while (field[x][y]!=0){
            x = (int)(Math.random()*row);
            y = (int)(Math.random()*col);
        }
        //sets the value of the tile to the parameter the method was called with
        //11 = hammer, 12 = widow, 13 = skrull
        field[x][y] = num;
        redraw();
    }

    //checks if the user won
    public boolean win(){
        //iterates through grid
        for (int i = 0 ; i < row ; i ++){
            for (int j = 0 ; j < col ; j ++){
                //if there is a mine that hasn't been flagged
                if (field[i][j] == 10 && flags[i][j] != 1)
                    //the user has not won (returns false)
                    return false;
            }
        }
        //if all mines have been flagged, they have won (returns true)
        return true;
    }
    public void makeDialogBox(){
        new AlertDialog.Builder(this)
                //The title on the Dialog
                .setTitle("BOOM!")
                //The message that will appear
                .setMessage("You clicked on an infinity stone.\nYou lose!")
                //What to do if the button is pressed
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which){
                        lose();
                    }
                }).show();
    }
    //if the user lost, it returns them to the opening screen when they close the dialog box
    public void lose(){
        Intent i = new Intent(this, opening.class);
        startActivity(i);
    }
    //checks how many mines a tile is touching
    public void neighbours(){
        //iterates through grid
        for (int i = 0 ; i < row ; i ++){
            for (int j = 0 ; j < col ; j++){
                //if the tile is not a mine (10)
                if (field[i][j] != 10) {
                    //if the surrounding tiles are in bounds and are a mine, adds one to the tile's value in the field array
                    if (i + 1 < row && field[i + 1][j] == 10)
                        field[i][j]++;
                    if (i + 1 < row && j + 1 < col && field[i + 1][j + 1] == 10)
                        field[i][j]++;
                    if (i + 1 < row && j - 1 >= 0 && field[i + 1][j - 1] == 10)
                        field[i][j]++;
                    if (i - 1 >= 0 && field[i - 1][j] == 10)
                        field[i][j]++;
                    if (i - 1 >= 0 && j + 1 < col && field[i - 1][j + 1] == 10)
                        field[i][j]++;
                    if (i - 1 >= 0 && j - 1 >= 0 && field[i - 1][j - 1] == 10)
                        field[i][j]++;
                    if (j + 1 < col && field[i][j + 1] == 10)
                        field[i][j]++;
                    if (j - 1 >= 0 && field[i][j - 1] == 10)
                        field[i][j]++;
                }
            }
        }
    }
    //rechecks neighbours, but with the fake mine
    //parameters are coordinates of the fake stone on the grid
    public void newNeighbours (int x, int y){
        //checks the tiles around the fake stone
        //if they're a number tile (their value in the field array minus eight is a negative number), add one to the value
        if (x + 1 < row && field [x + 1][y] - 8 <= 0)
            field[x + 1][y]++;
        if (x + 1 < row && y + 1 < col && field [x + 1][y + 1] - 8 <= 0)
            field[x + 1][y + 1]++;
        if (x + 1 < row && y - 1 >= 0 && field [x + 1][y - 1] - 8 <= 0)
            field[x + 1][y - 1]++;
        if (x - 1 >= 0 && field [x - 1][y] - 8 <= 0)
            field[x - 1][y]++;
        if (x - 1 >= 0 && y + 1 < col && field [x - 1][y + 1] - 8 <= 0)
            field[x - 1][y + 1]++;
        if (x - 1 >= 0 && y - 1 >= 0 && field [x - 1][y - 1] - 8 <= 0)
            field[x - 1][y - 1]++;
        if (y + 1 < col && field [x][y + 1] - 8 <= 0)
            field[x][y + 1]++;
        if (y - 1 >= 0 && field [x][y - 1] - 8 <= 0)
            field[x][y - 1]++;
    }
    //called when the flag button is clicked
    //toggles flag mode
    public void flag (View view){
        //if flag mode is off
        if (!flag)
            //turn it on
            flag = true;
        //if flag mode is on
        else
            //turn it off
            flag = false;
    }

    // called when reset button is clicked
    public void reset(View view){
        //iterated through grid
        for (int i = 0 ; i < row ; i ++){
            for (int j = 0 ; j < col ; j++){
                //hides all tiles
                showSquare[i][j] = 0;
                //unflag all tiles
                flags[i][j] = 0;
            }
        }
        redraw();
    }

    //called when hammer button is clicked
    public void hammer (View view) {
        //sets use hammer to true
        useHammer = true;
    }
    //called when widow button is clicked
    public void widow (View view){
        //linking to widow button in xml file
        ImageButton widow = (ImageButton) findViewById(R.id.widow);
        outerloop:
        //iterates through grid
        for (int i = 0 ; i < row ; i ++){
            for (int j = 0 ; j < col ; j ++){
                //if the tile being checked is hidden, and is a mine, and has not been flagged
                if (showSquare[i][j] == 0 && field[i][j] == 10 && flags[i][j] == 0) {
                    //flag it
                    flags[i][j] = 1;
                    //breaks both for loops to reveal only one mine
                    break outerloop;
                }

            }
        }
        redraw();
        //disables widow button
        widow.setEnabled(false);
    }
    //shows the whole grid
    public void show (View view){
        //iterates through grid
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                //shows square
                showSquare[i][j] = 1;
            }
        }
        redraw();
    }
}