import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Locale;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.lang3.time.DateUtils;

import biweekly.Biweekly;
import biweekly.ICalVersion;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.io.text.ICalWriter;
import biweekly.util.Duration;
import data.Song;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

/**
 * Created on: 2018-10-30 at 12:19 PM
 *
 * @author technophil98
 */
public class SheetsToCalendar {
   private static DateTimeFormatter formatter;

   public static void main(String[] args) throws IOException {
      JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home"));
      fileChooser.setFileFilter(new CSVFileFilter());
      fileChooser.setAcceptAllFileFilterUsed(false);

      final int returnVal = fileChooser.showDialog(null, "Convert");

      if (returnVal == JFileChooser.APPROVE_OPTION) {
         final List<Song> songs = readSongsFromFile(fileChooser);
         ICalendar iCalendar = createiCalFromSongs(songs);
         writeicsFile(iCalendar);
      }
   }

   private static void writeicsFile(ICalendar iCalendar) throws IOException {
      File file = new File("pratique.ics");
      try (ICalWriter writer = new ICalWriter(file, ICalVersion.V2_0)) {
         writer.write(iCalendar);
      }
   }

   private static ICalendar createiCalFromSongs(List<Song> songs) {
      ICalendar iCalendar = new ICalendar();
      for (int i = 0; i < songs.size(); i++) {
         final Song song = songs.get(i);
         final Song nextSong = i + 1 < songs.size() ? songs.get(i + 1) : null;

         VEvent songEvent = new VEvent();
         songEvent.setSummary("Pratique PolyJam | " + song.getNom());
         songEvent.setDateStart(song.getStartDate());
         if (nextSong != null) {
            songEvent.setDateEnd(nextSong.getStartDate());
         } else {
            songEvent.setDuration(Duration.builder().minutes(30).build());
         }

         songEvent.setDescription(song.getDescription());

         iCalendar.addEvent(songEvent);
      }
      return iCalendar;
   }

   private static List<Song> readSongsFromFile(JFileChooser fileChooser) throws IOException {
      BufferedReader reader = Files.newBufferedReader(fileChooser.getSelectedFile().toPath());

      final String practiceDateString = reader.readLine().trim();
      Song.setSongsDate(formatDate(practiceDateString));

      final CsvToBean<Song> csvReader = new CsvToBeanBuilder(reader)
            .withType(Song.class)
            .withIgnoreLeadingWhiteSpace(true)
            .withSkipLines(1)
            .build();

      return csvReader.parse();
   }

   private static LocalDate formatDate(String practiceDateString) {
      if (formatter == null) {
         formatter = new DateTimeFormatterBuilder()
               .parseCaseInsensitive()
               .append(DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.CANADA_FRENCH))
               .toFormatter();
      }
      return MonthDay.parse(practiceDateString, formatter).atYear(Year.now().getValue());
   }

   private static class CSVFileFilter extends FileFilter {
      @Override
      public boolean accept(File f) {
         return f.getName().endsWith(".csv");
      }

      @Override
      public String getDescription() {
         return "*.csv";
      }
   }
}
