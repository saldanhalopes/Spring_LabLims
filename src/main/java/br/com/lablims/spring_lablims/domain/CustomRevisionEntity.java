package br.com.lablims.spring_lablims.domain;

import jakarta.persistence.*;


import br.com.lablims.spring_lablims.config.CustomRevisionListener;
import lombok.*;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import java.util.Date;

@Entity
@RevisionEntity(value = CustomRevisionListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Table(name = "ta_revinfo_audit", schema = "audit")
public class CustomRevisionEntity extends DefaultRevisionEntity {

    private String userName;
    private String ip;
    @Transient
    private String revType;
    private String motivo;

    private static String motivoText;

    public static String getMotivoText() {
        return motivoText;
    }

    public static void setMotivoText(String motivoText) {
        CustomRevisionEntity.motivoText = motivoText;
    }

    public CustomRevisionEntity(Integer revisaoId, Date revisaoData, String userName, String ip, String revType, String motivo) {
        this.setId(revisaoId);
        this.setTimestamp(revisaoData.getTime());
        this.userName = userName;
        this.ip = ip;
        this.revType = revType;
        this.motivo = motivo;
    }

}


