package data;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;

/**
 * Created on: 2018-10-30 at 12:36 PM
 *
 * @author technophil98
 */
public class Song {

   private static LocalDate songsDate;

   public static void setSongsDate(LocalDate songsDate) {
      Song.songsDate = songsDate;
   }

   @CsvDate(value = "HH:mm")
   @CsvBindByPosition(position = 2, required = true)
   private Date startDate;

   public Date getStartDate() {
      return startDate;
   }

   public void setStartDate(Date startDate) {
      LocalTime startTime = LocalDateTime.ofInstant(startDate.toInstant(), ZoneId.systemDefault()).toLocalTime();
      this.startDate = Date.from(songsDate.atTime(startTime).atZone(ZoneId.systemDefault()).toInstant());
   }

   @CsvBindByPosition(position = 3, required = true)
   private String nom;

   public String getNom() {
      return nom;
   }

   @CsvBindByPosition(position = 4)
   private String chanteur1;

   @CsvBindByPosition(position = 5)
   private String chanteur2;

   @CsvBindByPosition(position = 6)
   private String chanteur3;

   @CsvBindByPosition(position = 7)
   private String guitare1;

   @CsvBindByPosition(position = 8)
   private String guitare2;

   @CsvBindByPosition(position = 9)
   private String bass;

   @CsvBindByPosition(position = 10)
   private String drum;

   @CsvBindByPosition(position = 11)
   private String keys;

   @CsvBindByPosition(position = 12)
   private String autre;

   public String getDescription() {
      return Arrays.stream(this.getClass().getDeclaredFields())
            .filter(field -> field.getType().equals(String.class))
            .map(this::createDescription)
            .collect(Collectors.joining("\n"));
   }

   private String createDescription(Field field) {
      final String fieldName = StringUtils.capitalize(field.getName());
      String fieldValue = "";
      try {
         fieldValue = ((String) field.get(this));
      } catch (IllegalAccessException e) {
         // Ignore
      }
      if (fieldValue == null)
         fieldValue = "-";

      return String.format("%s : %s", fieldName, fieldValue);
   }
}
