import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  FlatList,
  Image,
  StyleSheet,
  Alert,
} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { launchImageLibrary } from 'react-native-image-picker';

export default function ChatDetailScreen({ route, navigation }) {
  const { user } = route.params;

  const [me, setMe] = useState('');
  const [text, setText] = useState('');
  const [messages, setMessages] = useState([]);

  useEffect(() => {
    (async () => {
      const u = await AsyncStorage.getItem('username');
      setMe(u || 'guest');
    })();
  }, []);

  useEffect(() => {
    if (!me) return;
    navigation.setOptions({ title: user.name });
    loadMessages();
  }, [me]);

  const storageKey = `chat_${me}_${user.id}`;

  const loadMessages = async () => {
    const raw = await AsyncStorage.getItem(storageKey);
    const data = raw ? JSON.parse(raw) : [];
    setMessages(Array.isArray(data) ? data : []);
  };

  const saveMessages = async newMessages => {
    setMessages(newMessages);
    await AsyncStorage.setItem(storageKey, JSON.stringify(newMessages));
  };

  const upsertChatList = async targetUser => {
    const listKey = `chat_list_${me}`;
    const raw = await AsyncStorage.getItem(listKey);
    const list = raw ? JSON.parse(raw) : [];

    const exists = (Array.isArray(list) ? list : []).find(
      x => x.id === targetUser.id,
    );

    if (!exists) {
      const nextList = [targetUser, ...(Array.isArray(list) ? list : [])];
      await AsyncStorage.setItem(listKey, JSON.stringify(nextList));
    }
  };

  const sendText = async () => {
    if (!text.trim()) return;

    const newMsg = {
      id: Date.now().toString(),
      type: 'text',
      text: text.trim(),
      from: me,
      createdAt: Date.now(),
    };

    const next = [...messages, newMsg];
    setText('');
    await saveMessages(next);
    await upsertChatList(user);
  };

  const sendImage = async () => {
    const result = await launchImageLibrary({
      mediaType: 'photo',
      quality: 0.7,
    });

    if (result.didCancel) return;

    const image = result.assets?.[0];
    if (!image?.uri) return;

    const newMsg = {
      id: Date.now().toString(),
      type: 'image',
      uri: image.uri,
      from: me,
      createdAt: Date.now(),
    };

    const next = [...messages, newMsg];
    await saveMessages(next);
    await upsertChatList(user);

    Alert.alert(
      'Info',
      'Upload ke Firebase Storage belum aktif. Gambar hanya tersimpan lokal.',
    );
  };

  const renderItem = ({ item }) => {
    if (item.type === 'image') {
      return <Image source={{ uri: item.uri }} style={styles.image} />;
    }
    return <Text style={styles.bubble}>{item.text}</Text>;
  };

  return (
    <View style={styles.container}>
      <FlatList
        data={messages}
        keyExtractor={item => item.id}
        renderItem={renderItem}
        contentContainerStyle={{ padding: 12 }}
      />

      <View style={styles.inputRow}>
        <TextInput
          value={text}
          onChangeText={setText}
          placeholder="Ketik pesan..."
          style={styles.input}
        />

        <TouchableOpacity style={styles.btn} onPress={sendText}>
          <Text style={styles.btnText}>KIRIM</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.btnImg} onPress={sendImage}>
          <Text style={styles.btnText}>📷</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1 },
  bubble: {
    padding: 10,
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 6,
    marginBottom: 10,
  },
  image: {
    width: 180,
    height: 180,
    borderRadius: 8,
    marginBottom: 10,
  },
  inputRow: {
    flexDirection: 'row',
    padding: 10,
    borderTopWidth: 1,
    borderColor: '#ddd',
  },
  input: {
    flex: 1,
    borderWidth: 1,
    borderColor: '#ccc',
    paddingHorizontal: 10,
    marginRight: 6,
    borderRadius: 4,
  },
  btn: {
    backgroundColor: '#000',
    paddingHorizontal: 14,
    justifyContent: 'center',
    marginRight: 6,
    borderRadius: 4,
  },
  btnImg: {
    backgroundColor: '#000',
    paddingHorizontal: 14,
    justifyContent: 'center',
    borderRadius: 4,
  },
  btnText: { color: '#fff' },
});
