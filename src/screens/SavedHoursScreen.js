import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  TouchableOpacity,
  Alert,
  TextInput,
} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';

const SavedHoursScreen = () => {
  const [hours, setHours] = useState([]);
  const [editingId, setEditingId] = useState(null);
  const [editValue, setEditValue] = useState('');

  useEffect(() => {
    loadHours();
  }, []);

  const loadHours = async () => {
    try {
      const savedHours = await AsyncStorage.getItem('savedHours');
      if (savedHours) {
        setHours(JSON.parse(savedHours));
      }
    } catch (error) {
      Alert.alert('Błąd', 'Nie udało się załadować zapisanych godzin');
    }
  };

  const handleDelete = async (id) => {
    Alert.alert(
      'Potwierdzenie',
      'Czy na pewno chcesz usunąć ten wpis?',
      [
        {
          text: 'Anuluj',
          style: 'cancel',
        },
        {
          text: 'Usuń',
          style: 'destructive',
          onPress: async () => {
            try {
              const updatedHours = hours.filter(hour => hour.id !== id);
              await AsyncStorage.setItem('savedHours', JSON.stringify(updatedHours));
              setHours(updatedHours);
            } catch (error) {
              Alert.alert('Błąd', 'Nie udało się usunąć wpisu');
            }
          },
        },
      ]
    );
  };

  const handleEdit = async (id) => {
    const timeRegex = /^([0-9]{2}):([0-9]{2})$/;
    if (!timeRegex.test(editValue)) {
      Alert.alert('Błąd', 'Wprowadź czas w formacie hh:mm');
      return;
    }

    try {
      const updatedHours = hours.map(hour =>
        hour.id === id ? { ...hour, hours: editValue } : hour
      );
      await AsyncStorage.setItem('savedHours', JSON.stringify(updatedHours));
      setHours(updatedHours);
      setEditingId(null);
      setEditValue('');
    } catch (error) {
      Alert.alert('Błąd', 'Nie udało się zaktualizować wpisu');
    }
  };

  const renderItem = ({ item, index }) => (
    <View style={styles.row}>
      <Text style={styles.cell}>{index + 1}</Text>
      {editingId === item.id ? (
        <TextInput
          style={styles.editInput}
          value={editValue}
          onChangeText={setEditValue}
          placeholder="hh:mm"
          keyboardType="numeric"
        />
      ) : (
        <Text style={styles.cell}>{item.hours}</Text>
      )}
      <Text style={styles.cell}>{item.date}</Text>
      <View style={styles.buttonContainer}>
        {editingId === item.id ? (
          <TouchableOpacity
            style={[styles.button, styles.saveButton]}
            onPress={() => handleEdit(item.id)}
          >
            <Text style={styles.buttonText}>Zapisz</Text>
          </TouchableOpacity>
        ) : (
          <TouchableOpacity
            style={[styles.button, styles.editButton]}
            onPress={() => {
              setEditingId(item.id);
              setEditValue(item.hours);
            }}
          >
            <Text style={styles.buttonText}>Edytuj</Text>
          </TouchableOpacity>
        )}
        <TouchableOpacity
          style={[styles.button, styles.deleteButton]}
          onPress={() => handleDelete(item.id)}
        >
          <Text style={styles.buttonText}>Usuń</Text>
        </TouchableOpacity>
      </View>
    </View>
  );

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.headerCell}>Lp.</Text>
        <Text style={styles.headerCell}>Godziny</Text>
        <Text style={styles.headerCell}>Data</Text>
        <Text style={styles.headerCell}>Akcje</Text>
      </View>
      <FlatList
        data={hours}
        renderItem={renderItem}
        keyExtractor={item => item.id.toString()}
        style={styles.list}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  header: {
    flexDirection: 'row',
    padding: 15,
    backgroundColor: '#f0f0f0',
    borderBottomWidth: 1,
    borderBottomColor: '#ccc',
  },
  headerCell: {
    flex: 1,
    fontWeight: 'bold',
    textAlign: 'center',
  },
  row: {
    flexDirection: 'row',
    padding: 15,
    borderBottomWidth: 1,
    borderBottomColor: '#eee',
    alignItems: 'center',
  },
  cell: {
    flex: 1,
    textAlign: 'center',
  },
  buttonContainer: {
    flex: 2,
    flexDirection: 'row',
    justifyContent: 'center',
    gap: 10,
  },
  button: {
    padding: 8,
    borderRadius: 5,
    minWidth: 70,
    alignItems: 'center',
  },
  editButton: {
    backgroundColor: '#007AFF',
  },
  deleteButton: {
    backgroundColor: '#FF3B30',
  },
  saveButton: {
    backgroundColor: '#34C759',
  },
  buttonText: {
    color: '#fff',
    fontSize: 14,
    fontWeight: 'bold',
  },
  editInput: {
    flex: 1,
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 5,
    padding: 5,
    textAlign: 'center',
  },
  list: {
    flex: 1,
  },
});

export default SavedHoursScreen; 