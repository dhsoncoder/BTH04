package com.example.qlsv;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText edtmalop,edttenlop,edtsiso;
    Button btninsert,btnupdate,btndelete,btnquery;
    // Khai bao listview
    ListView lv;
    ArrayList<String> mylist;
    ArrayAdapter<String> myadapter;
    SQLiteDatabase mydatabase;
    int MAX_SISO_VALUE  = 50;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        edtmalop = findViewById(R.id.edtmalop);
        edtsiso = findViewById(R.id.edtsiso);
        edttenlop = findViewById(R.id.edttenlop);
        btndelete = findViewById(R.id.btndelete);
        btninsert = findViewById(R.id.btninsert);
        btnupdate = findViewById(R.id.btnupdate);
        //Tao listview

        lv=findViewById(R.id.lv);
        mylist = new ArrayList<>();
        myadapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,mylist);
        lv.setAdapter(myadapter);
        mydatabase = openOrCreateDatabase("qlsinhvien.db",MODE_PRIVATE,null);

        try {
            String sql ="CREATE TABLE tbllop(malop TEXT primary key, tenlop TEXT, siso INTEGER)";
            mydatabase.execSQL(sql);
        }catch (Exception e){
            Log.e("Error", "Table da ton tai");
        }
        reload();
        btninsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String malop = edtmalop.getText().toString().trim();
                String tenlop = edttenlop.getText().toString().trim();
                String sisoText = edtsiso.getText().toString().trim();

                // Kiểm tra xem các trường input có rỗng không
                if (malop.isEmpty() || tenlop.isEmpty() || sisoText.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra xem siso có phải là số nguyên dương không
                int siso;
                try {
                    siso = Integer.parseInt(sisoText);
                    if (siso <= 0) {
                        Toast.makeText(MainActivity.this, "Số lượng học sinh phải là số nguyên dương", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Số lượng học sinh không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra xem siso có quá lớn không (điều kiện tùy thuộc vào yêu cầu của ứng dụng)
                if (siso > MAX_SISO_VALUE) {
                    Toast.makeText(MainActivity.this, "Số lượng học sinh quá lớn", Toast.LENGTH_SHORT).show();
                    return;
                }

                ContentValues myvalue = new ContentValues();
                myvalue.put("malop", malop);
                myvalue.put("tenlop", tenlop);
                myvalue.put("siso", siso);
                String msg = "";
                if (mydatabase.insert("tbllop", null, myvalue) == -1) {
                    msg = "Lỗi ";
                } else {
                    msg = "Thêm thành công";
                }
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                reload();
            }
        });

        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String malop = edtmalop.getText().toString();
                int n = mydatabase.delete("tbllop","malop = ?",new String[]{malop});
                String msg ="";
                if(n==0){
                    msg = "Không có bản ghi để xóa";

                }else{
                    msg= "Bản ghi "+n+"đã bị xóa";
                }
                Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
                reload();
            }
        });
        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int siso = Integer.parseInt(edtsiso.getText().toString());
                String malop = edtmalop.getText().toString();
                ContentValues myvalue = new ContentValues();
                myvalue.put("siso",siso);
                int n= mydatabase.update("tbllop",myvalue,"malop =?",new String[]{malop});
                String msg ="";
                if(n==0){
                    msg = "Không có bản ghi được cập nhật";

                }else{
                    msg= "Bản ghi "+n+"đã được cập nhật";
                }
                Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
                reload();
            }

        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public  void reload(){
        mylist.clear();
        Cursor c = mydatabase.query("tbllop",null,null,null,null,null,null);
        c.moveToNext();
        String data = "";
        while(c.isAfterLast()==false){
            data = c.getString(0)+ " - "+ c.getString(1)+" - "+c.getString(2);
            c.moveToNext();
            mylist.add(data);

        }
        c.close();
        myadapter.notifyDataSetChanged();
    }
}