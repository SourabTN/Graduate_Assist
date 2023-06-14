package com.mdnhs.graduateassist.util;

import com.mdnhs.graduateassist.item.CommentList;

import java.util.List;

public class Events {

    // Event used to send message from login notify.
    public static class Login {
        private String login;

        public Login(String login) {
            this.login = login;
        }

        public String getLogin() {
            return login;
        }
    }

    // Event used to send message from comment notify.
    public static class AddComment {

        private String commentId, movieId, userId, userName, userImage, userImageSmall, commentText, commentDate, totalComment;

        public AddComment(String commentId, String movieId, String userId, String userName, String userImage, String userImageSmall, String commentText, String commentDate, String totalComment) {
            this.commentId = commentId;
            this.movieId = movieId;
            this.userId = userId;
            this.userName = userName;
            this.userImage = userImage;
            this.userImageSmall = userImageSmall;
            this.commentText = commentText;
            this.commentDate = commentDate;
            this.totalComment = totalComment;
        }

        public String getCommentId() {
            return commentId;
        }

        public String getMovieId() {
            return movieId;
        }

        public String getUserId() {
            return userId;
        }

        public String getUserName() {
            return userName;
        }

        public String getUserImage() {
            return userImage;
        }

        public String getUserImageSmall() {
            return userImageSmall;
        }

        public String getCommentText() {
            return commentText;
        }

        public String getCommentDate() {
            return commentDate;
        }

        public String getTotalComment() {
            return totalComment;
        }
    }

    //Event used to delete comment notify and total comment.
    public static class DeleteComment {

        private String totalComment, movieId, type;
        private List<CommentList> commentLists;

        public DeleteComment(String totalComment, String movieId, String type, List<CommentList> commentLists) {
            this.totalComment = totalComment;
            this.movieId = movieId;
            this.type = type;
            this.commentLists = commentLists;
        }

        public String getTotalComment() {
            return totalComment;
        }

        public String getMovieId() {
            return movieId;
        }

        public String getType() {
            return type;
        }

        public List<CommentList> getCommentLists() {
            return commentLists;
        }
    }

    //Event used to update profile
    public static class ProfileUpdate {

        private String string;

        public ProfileUpdate(String string) {
            this.string = string;
        }

        public String getString() {
            return string;
        }
    }

    //Event used to update remove and update image
    public static class ProImage {

        private String string, imagePath;
        private boolean isProfile, isRemove;

        public ProImage(String string, String imagePath, boolean isProfile, boolean isRemove) {
            this.string = string;
            this.imagePath = imagePath;
            this.isProfile = isProfile;
            this.isRemove = isRemove;
        }

        public String getString() {
            return string;
        }

        public String getImagePath() {
            return imagePath;
        }

        public boolean isProfile() {
            return isProfile;
        }

        public boolean isRemove() {
            return isRemove;
        }
    }

}
