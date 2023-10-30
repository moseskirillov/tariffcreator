package com.fmlogistic.tariffcreator.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "comment_files")
public class CommentFiles {

    public CommentFiles(String filename, String comment) {
        this.filename = filename;
        this.comment = comment;
    }

    @Id
    @Setter(AccessLevel.NONE)
    @SequenceGenerator(name = "comments_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comments_seq")
    private Long id;

    @Column(name = "filename")
    private String filename;

    @Column(name = "comment")
    private String comment;

}
