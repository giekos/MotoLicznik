import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import HomeScreen from './src/screens/HomeScreen';
import SavedHoursScreen from './src/screens/SavedHoursScreen';

const Stack = createNativeStackNavigator();

export default function App() {
  return (
    <NavigationContainer>
      <Stack.Navigator initialRouteName="Home">
        <Stack.Screen 
          name="Home" 
          component={HomeScreen} 
          options={{ title: 'MotoLicznik' }}
        />
        <Stack.Screen 
          name="SavedHours" 
          component={SavedHoursScreen} 
          options={{ title: 'Zapisane godziny' }}
        />
      </Stack.Navigator>
    </NavigationContainer>
  );
} 