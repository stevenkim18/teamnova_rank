package com.teamnova.teamnova_rank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class RankDescriptionActivity extends Dialog {
    private  TextView rankTitle,rankPoint,noramalRanking,rankView,rankName,rankLike,rankReply;

    private String rankWriterName;
    private RankData rankData;

    public void setRankData(RankData rankData) {
        this.rankData = rankData;
    }

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
//        setContentView(R.layout.activity_rank_description);


        rankName = findViewById(R.id.rank_name);
        rankLike = findViewById(R.id.rank_like);
        rankReply = findViewById(R.id.rank_reply);
//        rankView = findViewById(R.id.rank_view);


//        rankData = getIntent().getParcelableExtra("rankData");
//        rankWriterName = getIntent().getExtras().get("작성자명").toString();

//        Toast.makeText(this,"작성자면"+rankData.getRankWriter()+"댓글수"+rankData.getReplyCount()+"좋아요수"+rankData.getLikeCount()+"조회수"+rankData.getViewCount(), Toast.LENGTH_SHORT).show();
        Toast.makeText(RankDescriptionActivity,"작성자면"+rankData.getRankWriter(), Toast.LENGTH_SHORT).show();
    }
}
