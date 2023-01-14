package entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuestionDTO {
    private String question;
    private String[] odp = new String[4];
    private int correct;

    public void setOdp1(String odp1) {
        odp[0] = odp1;
    }

    public void setOdp2(String odp2) {
        odp[1] = odp2;
    }

    public void setOdp3(String odp3) {
        odp[2] = odp3;
    }

    public void setOdp4(String odp4) {
        odp[3] = odp4;
    }
}
