package com.naturpark;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.naturpark.data.PoiType;

import java.util.List;

public class PoiTypeListActivity extends AppCompatActivity
        implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private List<PoiType> _list_poi_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_type_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        _list_poi_type = new DbManager(this).queryPoiTypeList();
        init();
    }

    public void init() {
        Button button = (Button) findViewById(R.id.button_close_poi_type_list);
        button.setOnClickListener(this);

        TableLayout table = (TableLayout) findViewById(R.id.displayPoiTypeList);
        table.removeAllViews();

        for (PoiType poiType : _list_poi_type) {
            TableRow row = new TableRow(this);

            row.addView(_create_text_view("<" + poiType.id() + ">"));
            row.addView(_create_text_view(poiType.name()));
            row.addView(_create_check_box(poiType));
            table.addView(row);

        }
    }


    public void onCheckedChanged(CompoundButton button, boolean isChecked) {
        PoiType poiType = (PoiType)button.getTag();
        if(button.isChecked() && !poiType.is_visible()) {
            poiType.show();
            new DbManager(this).update(poiType);
        }
        else if (!button.isChecked() && poiType.is_visible()) {
            poiType.hide();
            new DbManager(this).update(poiType);
        }
    }

    public void onClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    private TextView _create_text_view(int val) {
        TextView view = new TextView(this);
        view.setText(Integer.toString(val));

        return view;
    }

    private TextView _create_text_view(String text) {
        TextView view = new TextView(this);
        view.setText(text);

        return view;
    }

    private CheckBox _create_check_box(PoiType poiType) {
        CheckBox checkBox = new CheckBox(this);
        checkBox.setOnCheckedChangeListener(this);
        checkBox.setTag(poiType);
        checkBox.setChecked(poiType.is_visible());

        return checkBox;
    }
}
