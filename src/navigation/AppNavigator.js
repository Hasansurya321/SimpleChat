import React, { useEffect, useState } from 'react';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import AsyncStorage from '@react-native-async-storage/async-storage';

import LoginScreen from '../screens/LoginScreen';
import RegisterScreen from '../screens/RegisterScreen';
import DashboardScreen from '../screens/DashboardScreen';
import UserListScreen from '../screens/UserListScreen';

import ChatListScreen from '../screens/ChatScreen/ChatListScreen';
import ChatDetailScreen from '../screens/ChatScreen/ChatDetailScreen';

const Stack = createNativeStackNavigator();

// SET TRUE SEKALI kalau mau reset auto-login, lalu balikin ke false
const FORCE_RESET_LOGIN = false;

export default function AppNavigator() {
  const [isLogin, setIsLogin] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const checkAutoLogin = async () => {
      try {
        if (FORCE_RESET_LOGIN) {
          await AsyncStorage.removeItem('isLogin');
          await AsyncStorage.removeItem('username');
          await AsyncStorage.removeItem('current_user');
        }

        const status = await AsyncStorage.getItem('isLogin');
        setIsLogin(status === 'true');
      } finally {
        setLoading(false);
      }
    };

    checkAutoLogin();
  }, []);

  if (loading) return null;

  return (
    <Stack.Navigator initialRouteName={isLogin ? 'Dashboard' : 'Login'}>
      <Stack.Screen
        name="Login"
        component={LoginScreen}
        options={{ headerShown: false }}
      />

      <Stack.Screen
        name="Register"
        component={RegisterScreen}
        options={{ headerShown: false }}
      />

      <Stack.Screen
        name="Dashboard"
        component={DashboardScreen}
        options={{ title: 'Dashboard' }}
      />

      <Stack.Screen
        name="ChatList"
        component={ChatListScreen}
        options={{ title: 'Chat' }}
      />

      <Stack.Screen
        name="UserList"
        component={UserListScreen}
        options={{ title: 'Lihat User Lain' }}
      />

      <Stack.Screen
        name="ChatDetail"
        component={ChatDetailScreen}
        options={{ title: 'Chat Detail' }}
      />
    </Stack.Navigator>
  );
}
