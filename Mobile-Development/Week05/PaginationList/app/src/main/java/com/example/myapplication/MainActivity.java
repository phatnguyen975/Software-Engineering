package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvSelectedName;
    private ListView lvContacts;
    private Button btn1, btn2, btn3;
    
    private List<Contact> allContacts;
    private List<Contact> displayedContacts;
    private ContactAdapter adapter;
    
    private final int ITEMS_PER_PAGE = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSelectedName = findViewById(R.id.tvSelectedName);
        lvContacts = findViewById(R.id.lvContacts);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);

        initMockData();

        displayedContacts = new ArrayList<>();
        adapter = new ContactAdapter(this, displayedContacts);
        lvContacts.setAdapter(adapter);
        
        loadPage(0);

        btn1.setOnClickListener(v -> loadPage(0));
        btn2.setOnClickListener(v -> loadPage(1));
        btn3.setOnClickListener(v -> loadPage(2));

        lvContacts.setOnItemClickListener((parent, view, position, id) -> {
            Contact selected = displayedContacts.get(position);
            tvSelectedName.setText("You choose: " + selected.getName());
            adapter.setSelectedPosition(position);
        });
    }

    private void initMockData() {
        allContacts = new ArrayList<>();
        String[] names = {
            "Nguyễn Văn A", "Lê Thị B", "Trần Văn C", "Phan Văn C", "Đinh Văn D",
            "Phạm Văn E", "Võ Thị F", "Hoàng Văn G", "Đặng Thị H", "Bùi Văn I",
            "Lý Văn J", "Chu Thị K", "Đỗ Văn L", "Hồ Thị M", "Ngô Văn N"
        };
        String[] phones = {
            "0989897873", "0967995843", "0907955843", "0967885811", "0988885231",
            "0912345678", "0923456789", "0934567890", "0945678901", "0956789012",
            "0967890123", "0978901234", "0989012345", "0990123456", "0901234567"
        };

        // Danh sách các icon hệ thống để mock ảnh
        int[] drawables = {
            android.R.drawable.ic_menu_myplaces,
            android.R.drawable.ic_menu_camera,
            android.R.drawable.ic_menu_call,
            android.R.drawable.ic_menu_view,
            android.R.drawable.ic_menu_edit,
            android.R.drawable.ic_menu_delete,
            android.R.drawable.ic_menu_share,
            android.R.drawable.ic_menu_save,
            android.R.drawable.ic_menu_search,
            android.R.drawable.ic_menu_send,
            android.R.drawable.ic_menu_info_details,
            android.R.drawable.ic_menu_gallery,
            android.R.drawable.ic_menu_mapmode,
            android.R.drawable.ic_menu_directions,
            android.R.drawable.ic_menu_agenda
        };

        for (int i = 0; i < 15; i++) {
            allContacts.add(new Contact(names[i], phones[i], drawables[i]));
        }
    }

    private void loadPage(int pageIndex) {
        displayedContacts.clear();
        int start = pageIndex * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, allContacts.size());

        for (int i = start; i < end; i++) {
            displayedContacts.add(allContacts.get(i));
        }

        // TỰ ĐỘNG CHỌN NGƯỜI ĐẦU TIÊN KHI LOAD TRANG
        if (!displayedContacts.isEmpty()) {
            Contact firstContact = displayedContacts.get(0);
            tvSelectedName.setText("You choose: " + firstContact.getName());
            adapter.setSelectedPosition(0);
        } else {
            adapter.setSelectedPosition(-1);
        }

        adapter.notifyDataSetChanged();
        updateButtonStates(pageIndex);
    }

    private void updateButtonStates(int activePage) {
        btn1.setEnabled(activePage != 0);
        btn2.setEnabled(activePage != 1);
        btn3.setEnabled(activePage != 2);
        
        btn1.setAlpha(activePage == 0 ? 0.3f : 1.0f);
        btn2.setAlpha(activePage == 1 ? 0.3f : 1.0f);
        btn3.setAlpha(activePage == 2 ? 0.3f : 1.0f);
    }
}
