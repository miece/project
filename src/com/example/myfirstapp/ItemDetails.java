package com.example.myfirstapp;

import com.parse.ParseFile;

/*
 * Create a class that will hold our Item data. 
 * Contains item id, details, title, category fields and their getters and setters.
 */

public class ItemDetails {
	
	private String id;
    private String title;
    private String details;
    private String category;
    private String author_artist;
    private String releaseDate;

    private String image;

    private ParseFile file;
	
    ItemDetails(String theId, String theTitle, String theContent, String theCategory, ParseFile theImage, String theAuthor_artist, String theReleaseDate) {
    	
        id = theId;
        title = theTitle;
        details = theContent;
        category = theCategory;

        author_artist = theAuthor_artist;
        releaseDate = theReleaseDate;
        file = theImage;
    }
    ItemDetails(String theId, String theTitle, String theContent, String theCategory, String theAuthor_artist, String theReleaseDate) {
    	
        id = theId;
        title = theTitle;
        details = theContent;
        category = theCategory;

        author_artist = theAuthor_artist;
        releaseDate = theReleaseDate;

    }
    

        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }
        public String getContent() {
            return details;
        }
        public void setContent(String content) {
            this.details = content;
        }
        
        public String getCategory() {
            return category;
        }
        public void setCategory(String category) {
            this.category = category;
        }
        public String getImage() {
            return image;
        }
        public void setImage(String image) {
            this.image = image;
        }
                
        public ParseFile getPhoto() {
        	return file;
        }
        public void setPhoto(ParseFile file) {
            this.file = file;
        }
        
        public String getAuthor_Artist() {
            return author_artist;
        }
        public void setAuthor_Artist(String theAuthor_artist) {
            this.author_artist = theAuthor_artist;
        }
        

        
        public String getReleaseDate() {
            return releaseDate;
        }
        public void setReleaseDate(String theReleaseDate) {
            this.releaseDate = theReleaseDate;
        }

        
        @Override
        public String toString() {
            return this.getTitle();
        }
     
    }