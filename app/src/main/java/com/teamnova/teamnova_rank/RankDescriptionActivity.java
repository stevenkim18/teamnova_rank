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
    private  TextView rankView,rankName,rankLike,rankReply,dialogTitle,rankDay;
    private ImageView thumbPathImg,playImgBtn;
    private RankData rankData;
    private Button closeDialogBtn;



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


        rankName = findViewById(R.id.writer_txt);
        rankLike = findViewById(R.id.like_txt);
        rankReply = findViewById(R.id.reply_txt);
        rankView = findViewById(R.id.view_txt);
        rankDay =findViewById(R.id.day_txt);
        thumbPathImg = findViewById(R.id.thumb_path_img);
        closeDialogBtn = findViewById(R.id.close_btn);
        playImgBtn = findViewById(R.id.play_img_btn);
        dialogTitle = findViewById(R.id.dialog_title);


        rankDay.setText(rankData.getCreateDate()+" ");//만든 날짜
        rankLike.setText(rankData.getLikeCount()+" "); //좋아요수 값을 연결해준다
        rankReply.setText(rankData.getReplyCount()+" ");//댓글수 값을 연결해준다
        rankView.setText(rankData.getViewCount()+" ");//조회수 값을 연결해준다
        rankName.setText(rankData.getRankWriter());//작성자명 값을 연결해준다
        dialogTitle.setText(rankData.getRankTitle());//제목 값을 연결해준다
//        Glide.with(context)
//                .load(rankData.getThumbPath())
//                .into(thumbPathImg);

        //Glide를 사용해서  썸네일 url을 이미지로 뿌려준다
        Glide.with(RankDescriptionActivity).load(rankData.getThumbPath()).into(thumbPathImg);//썸네일 url을 이미지로 값을 연결해준다

        //play버튼을 클릭한경우
        playImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //url주소를 가져온다
                String url = rankData.getDetailLink();
                //uri을 파싱한다
                Uri uri = Uri.parse(url);
                //uri로 이동시켜준다
                Intent urlIntent = new Intent(Intent.ACTION_VIEW, uri);

                //액티비티 실행
                RankDescriptionActivity.startActivity(urlIntent);

//                Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( rankData.getDetailLink() ) );
//                startActivity(browse);
            }
        });

        //닫기 버튼 클릭한경우
        closeDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //다이어로그 사라지게 한다
                dismiss();

            }
        });

     /*           rankData = getIntent().getParcelableExtra("rankData");
        rankWriterName = getIntent().getExtras().get("작성자명").toString();

        Toast.makeText(this,"작성자면"+rankData.getRankWriter()+"댓글수"+rankData.getReplyCount()+"좋아요수"+rankData.getLikeCount()+"조회수"+rankData.getViewCount(), Toast.LENGTH_SHORT).show();
        Toast.makeText(RankDescriptionActivity,"작성자면"+rankData.getRankWriter(), Toast.LENGTH_SHORT).show();*/
    }
}
