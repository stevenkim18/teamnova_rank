package com.teamnova.teamnova_rank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;


//RankDescriptionActivity = 다이얼로그
public class RankDescriptionActivity extends Dialog {
    private  TextView rankView,rankName,rankLike,rankReply,dialogTitle;
    private ImageView thumbPathImg,playImgBtn;
    private RankData rankData;
    private Button urlBtn;



    public void setRankData(RankData rankData) {
        this.rankData = rankData;
    }

    //RankDescriptionActivity 액티비티 생성
    private Activity RankDescriptionActivity;


    public RankDescriptionActivity(@NonNull Context context) {
        super(context);
        Activity RankDescriptionActivity = (context instanceof Activity) ? (Activity)context:null;
        if(RankDescriptionActivity != null){
            this.RankDescriptionActivity =RankDescriptionActivity;
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank_description);


        rankName = findViewById(R.id.rank_name);
        rankLike = findViewById(R.id.rank_like);
        rankReply = findViewById(R.id.rank_reply);
        rankView = findViewById(R.id.rank_view);
        thumbPathImg = findViewById(R.id.thumb_path_img);
        urlBtn = findViewById(R.id.btnDialog);
        playImgBtn = findViewById(R.id.play_img_btn);
        dialogTitle = findViewById(R.id.dialog_title);


        rankLike.setText(rankData.getLikeCount()+" ");
        rankReply.setText(rankData.getReplyCount()+" ");
        rankView.setText(rankData.getViewCount()+" ");
        rankName.setText(rankData.getRankWriter());
        dialogTitle.setText(rankData.getRankTitle());
//        Glide.with(context)
//                .load(rankData.getThumbPath())
//                .into(thumbPathImg);
        Glide.with(RankDescriptionActivity).load(rankData.getThumbPath()).into(thumbPathImg);

        playImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = rankData.getDetailLink();
                Uri uri = Uri.parse(url);
                Intent urlIntent = new Intent(Intent.ACTION_VIEW, uri);

                RankDescriptionActivity.startActivity(urlIntent);


//                Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( rankData.getDetailLink() ) );
//                startActivity(browse);
            }
        });

        urlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = rankData.getDetailLink();
                Uri uri = Uri.parse(url);
                Intent urlIntent = new Intent(Intent.ACTION_VIEW, uri);

//                RankDescriptionActivity.startActivity(urlIntent);
                dismiss();


//                Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( rankData.getDetailLink() ) );
//                startActivity(browse);
            }
        });

        //        rankData = getIntent().getParcelableExtra("rankData");
//        rankWriterName = getIntent().getExtras().get("작성자명").toString();

//        Toast.makeText(this,"작성자면"+rankData.getRankWriter()+"댓글수"+rankData.getReplyCount()+"좋아요수"+rankData.getLikeCount()+"조회수"+rankData.getViewCount(), Toast.LENGTH_SHORT).show();
//        Toast.makeText(RankDescriptionActivity,"작성자면"+rankData.getRankWriter(), Toast.LENGTH_SHORT).show();
    }
}
