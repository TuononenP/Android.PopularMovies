package com.petrituononen.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.NetworkOnMainThreadException;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.petrituononen.popularmovies.exceptions.ApiKeyNotFoundException;
import com.petrituononen.popularmovies.exceptions.NoInternetConnectionException;
import com.petrituononen.popularmovies.utilities.IOUtilities;
import com.petrituononen.popularmovies.utilities.PicassoUtils;
import com.petrituononen.popularmovies.utilities.TheMovieDbUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.AlternativeTitle;
import info.movito.themoviedbapi.model.Artwork;
import info.movito.themoviedbapi.model.ArtworkType;
import info.movito.themoviedbapi.model.Collection;
import info.movito.themoviedbapi.model.Credits;
import info.movito.themoviedbapi.model.Genre;
import info.movito.themoviedbapi.model.Language;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.MovieImages;
import info.movito.themoviedbapi.model.MovieList;
import info.movito.themoviedbapi.model.MovieTranslations;
import info.movito.themoviedbapi.model.MoviesAlternativeTitles;
import info.movito.themoviedbapi.model.Multi;
import info.movito.themoviedbapi.model.ProductionCompany;
import info.movito.themoviedbapi.model.ProductionCountry;
import info.movito.themoviedbapi.model.ReleaseInfo;
import info.movito.themoviedbapi.model.Reviews;
import info.movito.themoviedbapi.model.Translation;
import info.movito.themoviedbapi.model.Video;
import info.movito.themoviedbapi.model.core.IdElement;
import info.movito.themoviedbapi.model.core.MovieKeywords;
import info.movito.themoviedbapi.model.core.ResultsPage;
import info.movito.themoviedbapi.model.keywords.Keyword;
import info.movito.themoviedbapi.model.people.PersonCast;
import info.movito.themoviedbapi.model.people.PersonCrew;

/**
 * Created by Petri Tuononen on 24.1.2017.
 * This class represents MovieDb class but implements Parcelable.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
public class ParcelableMovieDb extends IdElement implements Multi, Parcelable {

    private TheMovieDbUtils movieDbUtils = new TheMovieDbUtils();
    private Context mContext;

    public ParcelableMovieDb(Context context, Cursor cursor) {
        mContext = context;
        this.setId(cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID)));
        this.originalTitle = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
        this.isFavorite = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE)) > 0;
        this.posterPath = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER));
        this.overview = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_SYNOPSIS));
        this.voteAverage = cursor.getFloat(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING));
        this.releaseDate = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
        this.popularity = cursor.getFloat(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POPULARITY));

        try {
            this.videos = movieDbUtils.getVideos(context, this.getId());
            this.reviews = movieDbUtils.getReviews(context, this.getId(), 0);
        } catch (NoInternetConnectionException e) {
            e.printStackTrace();
        } catch (ApiKeyNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.w("ParcelableMovieDb", e.getMessage());
            e.printStackTrace();
        }
    }

    public ParcelableMovieDb(Context context, MovieDb movieDb) {
        try {
            this.setId(movieDb.getId());
            this.title = movieDb.getTitle();
            this.originalTitle = movieDb.getOriginalTitle();
            this.popularity = movieDb.getPopularity();
            this.backdropPath = movieDb.getBackdropPath();
            this.posterPath = movieDb.getPosterPath();
            this.releaseDate = movieDb.getReleaseDate();
            this.adult = movieDb.isAdult();
            this.belongsToCollection = movieDb.getBelongsToCollection();
            this.budget = movieDb.getBudget();
            this.genres = movieDb.getGenres();
            this.homepage = movieDb.getHomepage();
            this.overview = movieDb.getOverview();
            this.imdbID = movieDb.getImdbID();
            this.productionCompanies = movieDb.getProductionCompanies();
            this.productionCountries = movieDb.getProductionCountries();
            this.revenue = movieDb.getRevenue();
            this.runtime = movieDb.getRuntime();
            this.spokenLanguages = movieDb.getSpokenLanguages();
            this.tagline = movieDb.getTagline();
            this.userRating = movieDb.getUserRating();
            this.voteAverage = movieDb.getVoteAverage();
            this.voteCount = movieDb.getVoteCount();
            this.status = movieDb.getStatus();
            this.alternativeTitles = (MoviesAlternativeTitles) movieDb.getAlternativeTitles();
            this.credits = movieDb.getCredits();
            this.images = (MovieImages) movieDb.getImages();
            this.keywords = (MovieKeywords) movieDb.getKeywords();
            this.releases = (TmdbMovies.ReleaseInfoResults) movieDb.getReleases();
            this.translations = (MovieTranslations) movieDb.getTranslations();
        } catch (Exception e) {
            Log.w("ParcelableMovieDb cstr", e.getMessage());
            e.printStackTrace();
        }

//        this.videos = (Video.Results) movieDb.getVideos();
        try {
            this.videos = movieDbUtils.getVideos(context, this.getId());
            this.reviews = movieDbUtils.getReviews(context, this.getId(), 0);
        } catch (NoInternetConnectionException e) {
            e.printStackTrace();
        } catch (ApiKeyNotFoundException e) {
            e.printStackTrace();
        } catch(Exception e) {
            Log.w("ParcelableMovieDb cstr", e.getMessage());
            e.printStackTrace();
        }

        try {
            this.similarMovies = (ResultsPage<MovieDb>) movieDb.getSimilarMovies();
            this.lists = (ResultsPage<MovieList>) movieDb.getLists();
        }
        catch (Exception ex) {
            Log.w("ParcelableMovieDb cstr", "Could not cast similar movies or lists");
        }
    }

    public ContentValues GetContentValues(Context context, boolean isFavorite) throws NetworkOnMainThreadException{
        if (this == null) {
            return null;
        }
        ContentValues values = new ContentValues();

        values.put(MovieContract.MovieEntry._ID, this.getId());
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, this.getId());
        values.put(MovieContract.MovieEntry.COLUMN_TITLE, this.getOriginalTitle());
        values.put(MovieContract.MovieEntry.COLUMN_FAVORITE, isFavorite);
        values.put(MovieContract.MovieEntry.COLUMN_POSTER, this.getPosterPath());
        values.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, this.getOverview());
        values.put(MovieContract.MovieEntry.COLUMN_RATING, this.getVoteAverage());
        values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, this.getReleaseDate());
        values.put(MovieContract.MovieEntry.COLUMN_POPULARITY, this.getPopularity());

//        try {
            String imageUrlString = new PicassoUtils().formMoviePosterUrl(this, context);
            URL imageUrl = null;
            try {

                imageUrl = new URL(imageUrlString);
            } catch (MalformedURLException e) {
                Log.w("ParcelableMovieDb cstr", e.getMessage());
                e.printStackTrace();
            }
            byte[] posterImageBytes = new IOUtilities().GetBytesFromUrl(imageUrl);
            values.put(MovieContract.MovieEntry.COLUMN_POSTER_BYTES, posterImageBytes);
//        } catch(Exception ex) {
//            ex.printStackTrace();
//            Log.w("ParcelableMovieDb cstr", "Could not load poster image bytes");
//        }


        return values;
    }

    @JsonProperty("title")
    private String title;
    @JsonProperty("original_title")
    private String originalTitle;

    @JsonProperty("popularity")
    private float popularity;

    @JsonProperty("backdrop_path")
    private String backdropPath;
    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("release_date")
    private String releaseDate;
    @JsonProperty("adult")
    private boolean adult;
    @JsonProperty("belongs_to_collection")
    private Collection belongsToCollection;
    @JsonProperty("budget")
    private long budget;
    @JsonProperty("genres")
    private List<Genre> genres;
    @JsonProperty("homepage")
    private String homepage;

    @JsonProperty("overview")
    private String overview;

    @JsonProperty("imdb_id")
    private String imdbID;

    @JsonProperty("production_companies")
    private List<ProductionCompany> productionCompanies;
    @JsonProperty("production_countries")
    private List<ProductionCountry> productionCountries;

    @JsonProperty("revenue")
    private long revenue;
    @JsonProperty("runtime")
    private int runtime;

    @JsonProperty("spoken_languages")
    private List<Language> spokenLanguages;

    @JsonProperty("tagline")
    private String tagline;

    @JsonProperty("rating")
    private float userRating;

    @JsonProperty("vote_average")
    private float voteAverage;
    @JsonProperty("vote_count")
    private int voteCount;

    @JsonProperty("status")
    private String status;

    // Appendable responses

    @JsonProperty("alternative_titles")
    private MoviesAlternativeTitles alternativeTitles;

    @JsonProperty("credits")
    private Credits credits;

    @JsonProperty("images")
    private MovieImages images;

    // note: it seems to be a flaw in their api, because a paged result would be more consistent
    @JsonProperty("keywords")
    private MovieKeywords keywords;

    @JsonProperty("releases")
    private TmdbMovies.ReleaseInfoResults releases;

    @JsonProperty("videos")
    private List<Video> videos;

    @JsonProperty("translations")
    private MovieTranslations translations;

    @JsonProperty("similar_movies")
    private ResultsPage<info.movito.themoviedbapi.model.MovieDb> similarMovies;

    @JsonProperty("reviews")
    private List<Reviews> reviews;

    @JsonProperty("lists")
    private ResultsPage<MovieList> lists;

    @JsonProperty("isFavorite")
    private boolean isFavorite;

    public String getBackdropPath() {
        return backdropPath;
    }


    public String getOriginalTitle() {
        return originalTitle;
    }


    public float getPopularity() {
        return popularity;
    }


    public String getPosterPath() {
        return posterPath;
    }


    public String getReleaseDate() {
        return releaseDate;
    }


    public String getTitle() {
        return title;
    }


    public boolean isAdult() {
        return adult;
    }


    public Collection getBelongsToCollection() {
        return belongsToCollection;
    }


    public long getBudget() {
        return budget;
    }


    public List<Genre> getGenres() {
        return genres;
    }


    public String getHomepage() {
        return homepage;
    }


    public String getImdbID() {
        return imdbID;
    }


    public String getOverview() {
        return overview;
    }


    public List<ProductionCompany> getProductionCompanies() {
        return productionCompanies;
    }


    public List<ProductionCountry> getProductionCountries() {
        return productionCountries;
    }


    public long getRevenue() {
        return revenue;
    }


    public int getRuntime() {
        return runtime;
    }


    public List<Language> getSpokenLanguages() {
        return spokenLanguages;
    }


    public String getTagline() {
        return tagline;
    }


    public float getVoteAverage() {
        return voteAverage;
    }


    public int getVoteCount() {
        return voteCount;
    }


    public String getStatus() {
        return status;
    }


    public List<AlternativeTitle> getAlternativeTitles() {
        return alternativeTitles != null ? alternativeTitles.getTitles() : null;
    }


    public List<PersonCast> getCast() {
        return credits != null ? credits.getCast() : null;
    }


    public List<PersonCrew> getCrew() {
        return credits != null ? credits.getCrew() : null;
    }


    public List<Artwork> getImages(ArtworkType... artworkTypes) {
        return images != null ? images.getAll(artworkTypes) : null;
    }


    public List<Keyword> getKeywords() {
        return keywords != null ? keywords.getKeywords() : null;
    }


    public List<ReleaseInfo> getReleases() {
        return releases != null ? releases.getCountries() : null;
    }


    public List<Video> getVideos() {
        return videos != null ? videos : null;
    }


    public List<Translation> getTranslations() {
        return translations != null ? translations.getTranslations() : null;
    }


    public List<info.movito.themoviedbapi.model.MovieDb> getSimilarMovies() {
        return similarMovies != null ? similarMovies.getResults() : null;
    }


    public List<MovieList> getLists() {
        return lists != null ? lists.getResults() : null;
    }


    public List<Reviews> getReviews() {
        return reviews != null ? reviews : null;
    }


    public Credits getCredits() {
        return credits;
    }


    public float getUserRating() {
        return userRating;
    }

    public boolean getIsFavorite() {
        return isFavorite;
    }

    @Override
    public String toString() {
        return title + " - " + releaseDate;
    }


    @Override
    public MediaType getMediaType() {
        return MediaType.MOVIE;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.getId());
        dest.writeString(this.title);
        dest.writeString(this.originalTitle);
        dest.writeFloat(this.popularity);
        dest.writeString(this.backdropPath);
        dest.writeString(this.posterPath);
        dest.writeString(this.releaseDate);
        dest.writeByte(this.adult ? (byte) 1 : (byte) 0);
        dest.writeSerializable(this.belongsToCollection);
        dest.writeLong(this.budget);
        dest.writeList(this.genres);
        dest.writeString(this.homepage);
        dest.writeString(this.overview);
        dest.writeString(this.imdbID);
        dest.writeList(this.productionCompanies);
        dest.writeList(this.productionCountries);
        dest.writeLong(this.revenue);
        dest.writeInt(this.runtime);
        dest.writeList(this.spokenLanguages);
        dest.writeString(this.tagline);
        dest.writeFloat(this.userRating);
        dest.writeFloat(this.voteAverage);
        dest.writeInt(this.voteCount);
        dest.writeString(this.status);
        dest.writeSerializable(this.alternativeTitles);
        dest.writeSerializable(this.credits);
        dest.writeSerializable(this.images);
        dest.writeSerializable(this.keywords);
        dest.writeSerializable(this.releases);
        dest.writeList(this.videos);
        dest.writeSerializable(this.translations);
        dest.writeSerializable(this.similarMovies);
        dest.writeList(this.reviews);
        dest.writeSerializable(this.lists);
        dest.writeByte(this.isFavorite ? (byte) 1 : (byte) 0);
    }

    public ParcelableMovieDb() {
    }

    protected ParcelableMovieDb(Parcel in) {
        this.setId(in.readInt());
        this.title = in.readString();
        this.originalTitle = in.readString();
        this.popularity = in.readFloat();
        this.backdropPath = in.readString();
        this.posterPath = in.readString();
        this.releaseDate = in.readString();
        this.adult = in.readByte() != 0;
        this.belongsToCollection = (Collection) in.readSerializable();
        this.budget = in.readLong();
        this.genres = new ArrayList<Genre>();
        in.readList(this.genres, Genre.class.getClassLoader());
        this.homepage = in.readString();
        this.overview = in.readString();
        this.imdbID = in.readString();
        this.productionCompanies = new ArrayList<ProductionCompany>();
        in.readList(this.productionCompanies, ProductionCompany.class.getClassLoader());
        this.productionCountries = new ArrayList<ProductionCountry>();
        in.readList(this.productionCountries, ProductionCountry.class.getClassLoader());
        this.revenue = in.readLong();
        this.runtime = in.readInt();
        this.spokenLanguages = new ArrayList<Language>();
        in.readList(this.spokenLanguages, Language.class.getClassLoader());
        this.tagline = in.readString();
        this.userRating = in.readFloat();
        this.voteAverage = in.readFloat();
        this.voteCount = in.readInt();
        this.status = in.readString();
        this.alternativeTitles = (MoviesAlternativeTitles) in.readSerializable();
        this.credits = (Credits) in.readSerializable();
        this.images = (MovieImages) in.readSerializable();
        this.keywords = (MovieKeywords) in.readSerializable();
        this.releases = (TmdbMovies.ReleaseInfoResults) in.readSerializable();
        this.videos = new ArrayList<>();
        in.readList(this.videos, Video.class.getClassLoader());
        this.translations = (MovieTranslations) in.readSerializable();
        this.similarMovies = (ResultsPage<MovieDb>) in.readSerializable();
        this.reviews = new ArrayList<>();
        in.readList(this.reviews, Reviews.class.getClassLoader());
        this.lists = (ResultsPage<MovieList>) in.readSerializable();
        this.isFavorite = in.readByte() != 0;
    }

    public static final Parcelable.Creator<ParcelableMovieDb> CREATOR = new Parcelable.Creator<ParcelableMovieDb>() {
        @Override
        public ParcelableMovieDb createFromParcel(Parcel source) {
            return new ParcelableMovieDb(source);
        }

        @Override
        public ParcelableMovieDb[] newArray(int size) {
            return new ParcelableMovieDb[size];
        }
    };
}
