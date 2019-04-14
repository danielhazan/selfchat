package com.example.selfchatex1;
import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class chatAdapter {
    interface chatBoxClickCallback{
        void onChatBoxClick(List<String> strings, int position);
    }
    static class chatHolder extends RecyclerView.ViewHolder {
        public final ImageView imageView;
        public final TextView textView;
        public chatHolder(@NonNull View itemView){
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView2);


        }
        public void set_text(String text){
            this.textView.setText(text);
        }


    }
    public static class chatAdapter1 extends RecyclerView.Adapter<chatHolder> {
//    public static class chatAdapter1 extends ListAdapter<chatHolder> {
        protected List<String> strings;
        protected List<Integer> msgIds;
        public chatAdapter1(List<String> strings){
            this.strings = strings;
        }

        public chatBoxClickCallback callback;
        public void addItem(String item,Integer msgId){
            this.strings.add(item);
            this.msgIds.add(msgId);
            notifyDataSetChanged();
        }
        public void removeItem(int position){
            this.strings.remove(position);
            this.msgIds.remove(position);
            notifyDataSetChanged();
        }
        public String getMsg(int position){
            return this.strings.get(position);
        }
        public Integer getMsgId(int position){
            return this.msgIds.get(position);
        }
        public ArrayList<String> getStrings(){
            return (ArrayList<String>) strings;
        }


        @NonNull
        @Override
        public chatHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view =  LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_per_chat,
                    viewGroup, false);
            final chatHolder holder = new chatHolder(view);

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            view.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v) {
                    String i;

                    if(callback != null) {
                        callback.onChatBoxClick(strings, holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                        notifyItemRangeChanged(holder.getAdapterPosition(), strings.size());
                   }
                    return false;

                }


            });

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull chatHolder chatHolder, int i) {
            chatHolder.set_text(strings.get(i));
        }

        @Override
        public int getItemCount() {
            return strings.size();
        }
    }


}
