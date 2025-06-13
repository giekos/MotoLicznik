import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  TextInput,
  Alert,
} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';

const HomeScreen = ({ navigation }) => {
  const [time, setTime] = useState(0);
  const [isRunning, setIsRunning] = useState(false);
  const [manualInput, setManualInput] = useState('');
  const [intervalId, setIntervalId] = useState(null);

  useEffect(() => {
    if (isRunning) {
      const id = setInterval(() => {
        setTime(prevTime => prevTime + 1);
      }, 1000);
      setIntervalId(id);
    } else {
      clearInterval(intervalId);
    }
    return () => clearInterval(intervalId);
  }, [isRunning]);

  const formatTime = (seconds) => {
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}`;
  };

  const handleSave = async () => {
    try {
      const savedHours = await AsyncStorage.getItem('savedHours');
      const hoursArray = savedHours ? JSON.parse(savedHours) : [];
      const newEntry = {
        id: Date.now(),
        hours: formatTime(time),
        date: new Date().toLocaleDateString('pl-PL'),
      };
      hoursArray.push(newEntry);
      await AsyncStorage.setItem('savedHours', JSON.stringify(hoursArray));
      Alert.alert('Sukces', 'Godziny zostały zapisane');
      setTime(0);
    } catch (error) {
      Alert.alert('Błąd', 'Nie udało się zapisać godzin');
    }
  };

  const handleManualSave = async () => {
    const timeRegex = /^([0-9]{2}):([0-9]{2})$/;
    if (!timeRegex.test(manualInput)) {
      Alert.alert('Błąd', 'Wprowadź czas w formacie hh:mm');
      return;
    }

    try {
      const savedHours = await AsyncStorage.getItem('savedHours');
      const hoursArray = savedHours ? JSON.parse(savedHours) : [];
      const newEntry = {
        id: Date.now(),
        hours: manualInput,
        date: new Date().toLocaleDateString('pl-PL'),
      };
      hoursArray.push(newEntry);
      await AsyncStorage.setItem('savedHours', JSON.stringify(hoursArray));
      Alert.alert('Sukces', 'Godziny zostały zapisane');
      setManualInput('');
    } catch (error) {
      Alert.alert('Błąd', 'Nie udało się zapisać godzin');
    }
  };

  return (
    <View style={styles.container}>
      <View style={styles.stopwatchContainer}>
        <Text style={styles.timer}>{formatTime(time)}</Text>
        <View style={styles.buttonContainer}>
          <TouchableOpacity
            style={styles.button}
            onPress={() => setIsRunning(!isRunning)}
          >
            <Text style={styles.buttonText}>{isRunning ? 'Stop' : 'Start'}</Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={[styles.button, !time && styles.buttonDisabled]}
            onPress={handleSave}
            disabled={!time}
          >
            <Text style={styles.buttonText}>Zapisz</Text>
          </TouchableOpacity>
        </View>
      </View>

      <View style={styles.manualInputContainer}>
        <TextInput
          style={styles.input}
          placeholder="Wprowadź czas (hh:mm)"
          value={manualInput}
          onChangeText={setManualInput}
          keyboardType="numeric"
        />
        <TouchableOpacity
          style={[styles.button, !manualInput && styles.buttonDisabled]}
          onPress={handleManualSave}
          disabled={!manualInput}
        >
          <Text style={styles.buttonText}>Zapisz</Text>
        </TouchableOpacity>
      </View>

      <TouchableOpacity
        style={styles.listButton}
        onPress={() => navigation.navigate('SavedHours')}
      >
        <Text style={styles.listButtonText}>Lista wartości</Text>
      </TouchableOpacity>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 20,
    backgroundColor: '#fff',
  },
  stopwatchContainer: {
    alignItems: 'center',
    marginTop: 20,
  },
  timer: {
    fontSize: 48,
    fontWeight: 'bold',
    marginBottom: 20,
  },
  buttonContainer: {
    flexDirection: 'row',
    justifyContent: 'center',
    gap: 20,
  },
  button: {
    backgroundColor: '#007AFF',
    padding: 15,
    borderRadius: 10,
    minWidth: 100,
    alignItems: 'center',
  },
  buttonDisabled: {
    backgroundColor: '#ccc',
  },
  buttonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
  },
  manualInputContainer: {
    marginTop: 40,
    alignItems: 'center',
  },
  input: {
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 10,
    padding: 15,
    width: '100%',
    marginBottom: 20,
    fontSize: 16,
  },
  listButton: {
    backgroundColor: '#34C759',
    padding: 15,
    borderRadius: 10,
    marginTop: 40,
    alignItems: 'center',
  },
  listButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
  },
});

export default HomeScreen; 