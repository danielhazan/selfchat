package com.example.selfchatex1;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import java.util.List;

public class chatAdapter {
    static class chatHolder extends RecyclerView.ViewHolder{
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
    public static class chatAdapter1 extends RecyclerView.Adapter<chatHolder>{
        protected List<String> strings;
        public chatAdapter1(List<String> strings){
            this.strings = strings;
        }
        public void addItem(String item){
            this.strings.add(item);
        }


        @NonNull
        @Override
        public chatHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = (View) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_per_chat,
                    viewGroup, false);
            return new chatHolder(view);
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
