package ru.tersoft.popmoviesapp;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MovieInfoLoader extends AsyncTask<Object, Object, Boolean> {
    /*
    AsyncTask that loads all needed movie info and makes a callback to DetailFragment
    */

    private HttpURLConnection mConnection;
    private MovieInfo mMovie;

    private DetailFragment.FragmentCallback mFragmentCallback;

    MovieInfoLoader(DetailFragment.FragmentCallback fragmentCallback) {
        mFragmentCallback = fragmentCallback;
    }

    protected Boolean doInBackground(Object... params) {
        // Parameters: 0 - movie position (int)
        mMovie = Data.getMovie((int)params[0]);
        String dataUrl = "http://api.themoviedb.org/3/movie/" + mMovie.mId + "?";
        String dataUrlParameters = "api_key=" + BuildConfig.TMDB_API_KEY +
                "&language=" + Data.getLocale().getLanguage() +
                "&append_to_response=releases,videos,reviews";

        try {
            URL url = new URL(dataUrl + dataUrlParameters);
            mConnection = (HttpURLConnection) url.openConnection();
            mConnection.setRequestMethod("GET");
            mConnection.setConnectTimeout(2000);
            // Reading answer with JsonReader
            InputStream is = mConnection.getInputStream();
            JsonReader jsonReader = new JsonReader(new InputStreamReader(is, "UTF-8"));
            try {
                readMovieInfo(jsonReader);
            } finally {
                jsonReader.close();
            }
        } catch (Exception e) {
            return false;
        } finally {
            if (mConnection != null) {
                mConnection.disconnect();
            }
        }
        return true;
    }

    protected void onPostExecute(Boolean i) {
        mFragmentCallback.onTaskDone(i);
    }

    public void readMovieInfo(JsonReader reader) throws IOException {
        // Initialize all needed variables
        String backdropPath = null, movieName = null, desc = null,
                home = null, localReleaseDate, date = null;
        double rating = 0;
        int runtime = 0;
        long budget = 0;
        List<String> genres = new ArrayList<>();
        boolean isLocal = false;
        List<Trailer> videos = new ArrayList<>();

        reader.beginObject();
        while (reader.hasNext()) {

            if(reader.peek() != JsonToken.NULL) {

                String name = reader.nextName();

                switch (name) {
                    case "backdrop_path":
                        // Backdrop image
                        if (reader.peek() != JsonToken.NULL) {
                            backdropPath = reader.nextString();
                        } else reader.skipValue();
                        break;
                    case "title":
                        // Movie's title
                        movieName = reader.nextString();
                        break;
                    case "vote_average":
                        // Movie's rating
                        if (reader.peek() != JsonToken.NULL) {
                            rating = reader.nextDouble();
                        } else reader.skipValue();
                        break;
                    case "release_date":
                        // Movie's release date in USA
                        if (reader.peek() != JsonToken.NULL) {
                            date = reader.nextString();
                        } else reader.skipValue();
                        break;
                    case "overview":
                        // Movie's description
                        if (reader.peek() != JsonToken.NULL) {
                            desc = reader.nextString();
                        } else reader.skipValue();
                        break;
                    case "runtime":
                        // Movie's runtime
                        if (reader.peek() != JsonToken.NULL) {
                            runtime = reader.nextInt();
                        } else reader.skipValue();
                        break;
                    case "homepage":
                        // Movie's homepage url
                        if (reader.peek() != JsonToken.NULL) {
                            home = reader.nextString();
                        } else reader.skipValue();
                        break;
                    case "budget":
                        // Movie's budget
                        if (reader.peek() != JsonToken.NULL) {
                            budget = reader.nextLong();
                        } else reader.skipValue();
                        break;
                    case "genres":
                        // Movie's genres list
                        if (reader.peek() != JsonToken.NULL) {
                            genres = readGenres(reader);
                        } else reader.skipValue();
                        break;
                    case "releases":
                        // Movie's local release date by country code
                        if (reader.peek() != JsonToken.NULL) {
                            localReleaseDate = readReleaseDate(reader);
                            if (localReleaseDate != null) {
                                // Found local release date
                                date = localReleaseDate;
                                isLocal = true;
                            }
                        } else reader.skipValue();
                        break;
                    case "videos":
                        // Movie available trailers
                        if (reader.peek() != JsonToken.NULL) {
                            videos = readVideos(reader);
                        } else reader.skipValue();
                        break;
                    default:
                        reader.skipValue();
                        break;
                }

            } else reader.skipValue();

        }
        reader.endObject();
        mMovie.addData(movieName, desc, backdropPath, date, isLocal,
                (float)rating, budget, runtime, genres, home, videos);
    }

    private List<String> readGenres(JsonReader reader) throws IOException {
        List<String> genres = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            if(reader.peek() != JsonToken.NULL) {
                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    if (name.equals("name")) {
                        if (reader.peek() != JsonToken.NULL) {
                            genres.add(reader.nextString());
                        } else reader.skipValue();
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
            } else reader.skipValue();
        }
        reader.endArray();

        return genres;
    }

    private String readReleaseDate(JsonReader reader) throws IOException {
        String releaseDate = null, countryCode = null;

        reader.beginObject();
        reader.nextName();
        reader.beginArray();
        while (reader.hasNext()) {

            if(reader.peek() != JsonToken.NULL) {
                reader.beginObject();
                while (reader.hasNext()) {

                    String name = reader.nextName();
                    switch (name) {
                        case "iso_3166_1":
                            if (reader.peek() != JsonToken.NULL) {
                                // Get country code
                                countryCode = reader.nextString();
                            } else reader.skipValue();
                            break;
                        case "release_date":
                            // Check country code equals device's language
                            if (countryCode != null) {
                                if (reader.peek() != JsonToken.NULL && countryCode.equals(Data.getLocale().getCountry())) {
                                    releaseDate = reader.nextString();
                                    break;
                                } else reader.skipValue();
                            }
                            break;
                        default:
                            reader.skipValue();
                            break;
                    }

                }
                reader.endObject();

            } else reader.skipValue();

        }
        reader.endArray();
        reader.endObject();

        return releaseDate;
    }

    private List<Trailer> readVideos(JsonReader reader) throws IOException {
        List<String> videoNames = new ArrayList<>();
        List<String> videoUrls = new ArrayList<>();
        List<Trailer> videos = new ArrayList<>();

        reader.beginObject();
        reader.nextName();
        reader.beginArray();
        while (reader.hasNext()) {

            if(reader.peek() != JsonToken.NULL) {
                reader.beginObject();
                while (reader.hasNext()) {

                    String name = reader.nextName();
                    switch (name) {
                        case "key":
                            if (reader.peek() != JsonToken.NULL) {
                                videoUrls.add(reader.nextString());
                            } else reader.skipValue();
                            break;
                        case "name":
                            if (reader.peek() != JsonToken.NULL) {
                                videoNames.add(reader.nextString());
                            } else reader.skipValue();
                            break;
                        default:
                            reader.skipValue();
                            break;
                    }

                }
                reader.endObject();

            } else reader.skipValue();

        }
        reader.endArray();
        reader.endObject();

        for(int i = 0; i < videoNames.size(); i++) {
            videos.add(new Trailer(videoUrls.get(i), videoNames.get(i)));
        }
        return videos;
    }
}