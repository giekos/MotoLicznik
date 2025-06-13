# MotoLicznik

Aplikacja do liczenia godzin pracy silnika zaburtowego. Pozwala na śledzenie czasu pracy silnika za pomocą stopera lub ręcznego wprowadzania wartości.

## Funkcje

- Stoper do liczenia godzin pracy silnika
- Ręczne wprowadzanie czasu w formacie hh:mm
- Lista zapisanych wartości z możliwością edycji i usuwania
- Zapisywanie danych w pamięci urządzenia

## Wymagania

- Node.js (wersja 14 lub nowsza)
- npm lub yarn
- React Native CLI
- Android Studio (dla systemu Android)
- Xcode (dla systemu iOS, tylko na macOS)

## Instalacja

1. Sklonuj repozytorium:
```bash
git clone [URL_REPOZYTORIUM]
cd MotoLicznik
```

2. Zainstaluj zależności:
```bash
npm install
# lub
yarn install
```

3. Dla systemu Android:
   - Otwórz projekt w Android Studio
   - Uruchom emulator lub podłącz urządzenie fizyczne
   - Uruchom aplikację:
```bash
npm run android
# lub
yarn android
```

4. Dla systemu iOS (tylko na macOS):
   - Zainstaluj zależności CocoaPods:
```bash
cd ios
pod install
cd ..
```
   - Uruchom aplikację:
```bash
npm run ios
# lub
yarn ios
```

## Użycie

1. Ekran główny:
   - Użyj przycisków Start/Stop do kontrolowania stopera
   - Zapisz zmierzony czas przyciskiem "Zapisz"
   - Wprowadź czas ręcznie w polu tekstowym (format hh:mm)
   - Przejdź do listy zapisanych wartości przyciskiem "Lista wartości"

2. Ekran listy wartości:
   - Przeglądaj zapisane wartości
   - Edytuj lub usuń wybrane wpisy
   - Wartości są automatycznie zapisywane w pamięci urządzenia

## Technologie

- React Native
- React Navigation
- AsyncStorage
- React Native CLI 