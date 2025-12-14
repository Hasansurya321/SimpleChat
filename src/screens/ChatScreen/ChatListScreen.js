import React, { useCallback, useState } from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  FlatList,
  StyleSheet,
} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { useFocusEffect } from '@react-navigation/native';

export default function ChatListScreen({ navigation }) {
  const [chats, setChats] = useState([]);

  // 🔑 FIX: namespace storage per user
  const loadChats = async () => {
    const me = await AsyncStorage.getItem('username');
    const raw = await AsyncStorage.getItem(`chat_list_${me || 'guest'}`);
    const data = raw ? JSON.parse(raw) : [];
    setChats(Array.isArray(data) ? data : []);
  };

  useFocusEffect(
    useCallback(() => {
      loadChats();
    }, []),
  );

  const openChat = user => {
    navigation.navigate('ChatDetail', { user });
  };

  const renderItem = ({ item }) => (
    <TouchableOpacity style={styles.item} onPress={() => openChat(item)}>
      <Text style={styles.name}>{item.name}</Text>
    </TouchableOpacity>
  );

  return (
    <View style={styles.container}>
      <FlatList
        data={chats}
        keyExtractor={item => String(item.id)}
        renderItem={renderItem}
        ListEmptyComponent={
          <Text style={styles.empty}>Belum ada history chat</Text>
        }
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 16 },
  item: {
    padding: 14,
    borderWidth: 1,
    borderColor: '#ccc',
    marginBottom: 10,
    borderRadius: 6,
  },
  name: { fontSize: 16 },
  empty: { marginTop: 30, textAlign: 'center' },
});
