package brewin.mark.motionalarm;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "tbl_trivia")
public class Trivia {
    @PrimaryKey(autoGenerate = true)
    private int id = 0;

    @NonNull
    private String question;

    @NonNull
    private String answer;

    public Trivia(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}
