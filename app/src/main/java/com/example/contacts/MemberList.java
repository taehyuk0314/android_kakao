package com.example.contacts;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MemberList extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_list);
        Context _this = MemberList.this;
        ItemList query = new ItemList(_this);
        ListView memberList = findViewById(R.id.memberList);


        memberList.setAdapter(
                new MemberAdapter(_this, (ArrayList<Index.Member>)new Index.ISupplier() {
                    @Override
                    public Object get() {
                        return query.get();
                    }
                }.get())
        );
        memberList.setOnItemClickListener(
                (AdapterView<?> p, View v, int i, long l)->{
                    Index.Member m = (Index.Member) memberList.getItemAtPosition(i);
                    Toast.makeText(_this,m.name,Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(_this, MemberDetail.class);
                    intent.putExtra("seq", m.seq);
                    startActivity(intent);
                });

         memberList.setOnItemLongClickListener(
                 (AdapterView<?> p, View v, int i, long l)->{
                 Index.Member m = (Index.Member) memberList.getItemAtPosition(i);
                 new AlertDialog.Builder(_this)
                         .setTitle("DELETE")
                         .setMessage("정말 삭제할꺼임?")
                         .setPositiveButton(
                                 android.R.string.yes,
                                 new DialogInterface.OnClickListener() {
                                     @Override
                                     public void onClick(DialogInterface dialog, int which) {
                                         //삭제 쿼리실행
                                         Toast.makeText(_this,"삭제완료~",Toast.LENGTH_LONG).show();
                                         startActivity(new Intent(_this, MemberList.class));
                                     }
                                 }
                         )
                         .setNegativeButton(
                                 android.R.string.no,
                                 new DialogInterface.OnClickListener() {
                                     @Override
                                     public void onClick(DialogInterface dialog, int which) {
                                         Toast.makeText(_this,"삭제취소",Toast.LENGTH_LONG).show();
                                     }
                                 }
                         )
                         .show();
                 return true;
         });


    }
    private class MemberListQuery extends Index.QueryFactory{
        SQLiteOpenHelper helper;
        public MemberListQuery(Context _this) {
            super(_this);
            helper = new Index.SQLiteHelper(_this);
        }

        @Override
        public SQLiteDatabase getDatabase() {
            return helper.getReadableDatabase();
        }
    }
    private class ItemList extends MemberListQuery{

        public ItemList(Context _this) {
            super(_this);
        }

        public ArrayList<Index.Member> get(){
            ArrayList<Index.Member> list = new ArrayList<>();
            Cursor c = this.getDatabase().rawQuery(
                    " SELECT * FROM MEMBER ", null
            );
            Index.Member m = null;
            if(c != null){
                while(c.moveToNext()){
                    m = new Index.Member();
                    m.seq = Integer.parseInt(c.getString(c.getColumnIndex(Index.MSEQ)));
                    m.name = c.getString(c.getColumnIndex(Index.MNAME));
                    m.pw = c.getString(c.getColumnIndex(Index.MPW));
                    m.email = c.getString(c.getColumnIndex(Index.MEMAIL));
                    m.phone = c.getString(c.getColumnIndex(Index.MPHONE));
                    m.addr = c.getString(c.getColumnIndex(Index.MEMAIL));
                    m.photo = c.getString(c.getColumnIndex(Index.MPHOTO));
                    Log.d("멤버정보 :  ",m.name);
                    list.add(m);

                }
                Toast.makeText(_this, "회원의 수"+list.size(),Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(_this, "등록된 회원이 없음",Toast.LENGTH_LONG).show();

            }
            return list;
        }

    }
    private class MemberAdapter extends BaseAdapter{
        ArrayList<Index.Member> list;
        LayoutInflater inflater;
        Context _this;

        public MemberAdapter(Context _this,ArrayList<Index.Member> list ) {
            this.list = list;
            this._this = _this;
            this.inflater = LayoutInflater.from(_this);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View v, ViewGroup g) {
            ViewHolder holder;
            if(v == null){
                v = inflater.inflate(R.layout.member_item, null);
                holder = new ViewHolder();
                holder.photo = v.findViewById(R.id.profile);
                holder.name = v.findViewById(R.id.name);
                holder.phone = v.findViewById(R.id.phone);
                v.setTag(holder);
            }else{
                holder = (ViewHolder) v.getTag();
            }
            ItemPhoto query = new ItemPhoto(_this);
            query.seq = list.get(i).seq+"";

            holder.photo
                    .setImageDrawable(
                            getResources()
                                    .getDrawable(
                                            getResources()
                                                    .getIdentifier(
                                                            _this.getPackageName()+":drawable/"
                                                                    +((String)new Index.ISupplier() {
                                                                @Override
                                                                public Object get() {
                                                                    return query.get();
                                                                }
                                                            }.get()),
                                                            null,
                                                            null),
                                            _this.getTheme()
                                    )
                    );

            holder.name.setText(list.get(i).name);
            holder.phone.setText(list.get(i).phone);
            return v;
        }
    }
    static class ViewHolder{
        ImageView photo;
        TextView name, phone;
    }

    private class MemberPhotoQuery extends Index.QueryFactory {
        SQLiteOpenHelper helper;
        public MemberPhotoQuery(Context _this) {
            super(_this);
            helper = new Index.SQLiteHelper(_this);
        }

        @Override
        public SQLiteDatabase getDatabase() {
            return helper.getReadableDatabase();
        }
    }

    private class ItemPhoto extends MemberListQuery{
        String seq;
        public ItemPhoto(Context _this) {
            super(_this);
        }
        public String get(){
            Cursor c = getDatabase()
                    .rawQuery(String.format(
                            " SELECT %s FROM %s "
                                    +" WHERE %s LIKE '%s' "
                            ,Index.MPHOTO, Index.MEMBERS,
                            Index.MSEQ, seq), null
                    );

            String result = "";
            if(c != null){
                if(c.moveToNext()){
                    result = c.getString(c.getColumnIndex(Index.MPHOTO));
                }
            }
            return result;
        }

    }



}