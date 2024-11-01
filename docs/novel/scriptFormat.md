# Формат хранения сюжета

Сюжет игры (как одиночной, так и командной) хранится в формате JSON. Сценарии для разных игровых режимов и разных ролей хранятся отдельно.

Файл сюжета разделяется на несколько частей:
- метаданные;
- объявление персонажей и их поз (со ссылками на файлы картинок) (ключ `characters`);
- объявление локаций (со ссылками на файлы картинок) (ключ `locations`);
- объявление музыки (со ссылками на файлы аудио) (ключ `music`);
- объявление глав;
- объявление фреймов и связи между ними (ключ `frames`);
  - фреймы основного сюжета (`main`);
  - концовки, условия их наступления, фреймы для каждой концовки (`endings`); 
- объявление заданий (ключ `tasks`).

## Общая структура (Toplevel)

Топ-левел JSON-файл имеет следующую структуру:

```json
{
  "metadata": { ... },
  "characters": { ... },
  "locations": { ... },
  "music": { ... },
  "startChapter": ...,
  "chapters": { ... },
  "frames": {
    "main": { ... },
    "endings": { ... }
  },
  "tasks": { ... },
  "knowledge": { ... },
}
```

- `metadata`: объект, содержащий информацию о сценарии.
- `characters`: словарь с описанием персонажей (ключ: `id` персонажа).
- `locations`: словарь с описанием локаций (ключ: `id` локации).
- `startChapter`: `id` начальной главы.
- `chapters`: словарь с главами и их данными (ключ: `id` главы).
- `music`: словарь с описанием музыкальных треков (ключ: `id` трека).
- `frames`: объект, содержащий фреймы основного сюжета и концовок.
  - `main`: словарь, содержащий главы (ключ: `id` главы). Каждая глава -- словарь фреймов (ключ -- `id` фрейма).
  - `endings`: словарь, содержащий концовки (ключ: `id` концовки). Каждая концовка -- объект с данными и списком фреймов.
- `tasks`: словарь с описанием заданий (ключ: `id` задания).
- `knowledge`: словарь с описанием открываемых знаний в базе знаний Prolog (ключ -- `id` знания, значение -- объект знания)

## Метаданные

Секция `metadata` содержит общую информацию о сценарии:

- `name`: Имя сценария (`==` имени файла без суффикса языка).
- `gamemode`: Режим игры: `single`, `pair`.
- `protagonist`: Имя протагониста.
- `author`: Имя автора сценария.
- `version`: Версия сценария.
- `language`: Код локали или языка сценария.

Пример:
```json
{
    "name": "ChapterOne",
    "gamemode": "single",
    "protagonist": "steve",
    "author": "Vad1mChK",
    "version": "1.0",
    "locale": "ru-RU"
}
```

## Персонажи

Секция `characters` содержит информацию о персонажах, используемых в игре. Каждый персонаж имеет уникальный идентификатор `id` и набор атрибутов.

### Структура персонажа
Каждый персонаж представлен объектом со следующими полями:

- `id`: Уникальный идентификатор персонажа.
- `name`: Отображаемое имя персонажа.
- `sprites`: Словарь поз персонажа (ключ — `id` позы, значение — путь к файлу изображения). Может быть пустым, если персонажа на экране не видно.
- `defaultPose`: `id` позы по умолчанию. Может быть `null`, если персонажа на экране не видно.

Пример:

```json
{
    "id": "steve",
    "name": "Стив",
    "defaultPose": "neutral",
    "sprites": {
      "neutral": "images/characters/steve/neutral.png",
      "happy": "images/characters/steve/happy.png",
      "sad": "images/characters/steve/sad.png"
    }
}
```

## Локации

Секция `locations` описывает различные места, где происходят события игры.

### Структура локации
Каждая локация представлена объектом со следующими полями:

- `id`: Уникальный идентификатор локации.
- `name`: Название локации.
- `background`: Путь к фоновому изображению локации.

Пример:
```json
{
    "id": "village_square",
    "name": "Деревенская площадь",
    "background": "images/locations/village_square.png",
}
```

## Музыка

Секция `music` содержит информацию о музыкальных треках, используемых в игре.

### Структура музыкального трека

Каждый трек представлен объектом с полями:

- `id`: Уникальный идентификатор трека.
- `name`: Название трека.
- `performer`: Исполнитель трека.
- `file`: Путь к аудиофайлу.
- `loop`: Булево значение, указывающее, нужно ли зациклить трек (по умолчанию `true`).

Пример:

```json
{
    "id": "background_theme",
    "name": "Default Theme",
    "performer": "John Doe",
    "file": "audio/music/background_theme.mp3",
}
```

## Главы

Секция `chapters` позволяет структурировать сюжет на отдельные части.

### Структура главы
Каждая глава содержит:

- `id`: Уникальный идентификатор главы.
- `title`: Название главы.
- `startFrame`: `id` первого фрейма главы.
- `nextChapter`: `id` следующей главы.
- `waitForPartner`: для парной игры -- требуется ли ждать партнёра дл перехода на следующую главу. Булевое значение, по умолчанию `false`.
- `knowledge`: список `id` знаний, по умолчанию открытых на момент начала главы. По умолчанию -- пустой список.

Пример:
```json
{
    "id": "chapter1",
    "title": "Пробуждение",
    "startFrame": "frame_001",
    "knowledge": [ "i_am_steve" ],
}
```

## Фреймы

### Структура фрейма
Фрейм описывает отдельный момент истории и содержит следующие поля:

- `id`: Уникальный идентификатор фрейма.
- `location`: `id` локации, отображаемой на фоне.
- `characters`: Список персонажей в сцене, их позиций и поз. Если не указать, сохраняются персонажи из предыдущего фрейма.
  - `id`: `id` персонажа.
  - `position`: Позиция на экране (`left`, `center`, `right`). По умолчанию -- `center`.
  - `pose`: `id` позы персонажа (опционально; если отсутствует, используется `defaultPose` персонажа).
  - `hidden`: булево значение, указывающее, скрыт ли персонаж (по умолчанию: `false`).
- `dialogue`: Текст диалога.
- `speaker`: `id` персонажа, говорящего в данный момент (опционально; если персонаж один, то говорит именно он).
- `choices`: Список выборов, доступных игроку (опционально).
  - `text`: Текст варианта выбора, отображаемый игроку.
  - `nextFrame`: `id` фрейма, к которому ведет данный выбор. 
- `nextFrame`: `id` следующего фрейма (может отсутствовать, если есть `choices`).
- `effects`: Список эффектов, применяемых в данном фрейме (опционально).

Поля `choices` и `nextFrame` могут отсутствовать в концовках, так как там нет ветвления, фреймы хранятся в массиве и исполняются последовательно.

### Условия

Условия используются для определения, когда определённый эффект или выбор должны быть доступны.

Поддерживаемые типы условий:
- `hasKnowledge`: Проверяет, есть ли у игрока определённое знание.
```json
{ "hasKnowledge": "knowledge_id" }
```
- `partnerDeadOnChapter`: Проверяет, мёртв ли партнёр (получил ли game over) и, если да, на какой главе умер. Для одиночной игры проверка всегда возвращает `false`.
```json
{ "partnerDeadOnChapter": "chapter_5" }
```
- `partnerCurrentlyOnChapter`: Проверяет, находится ли сейчас партнёр на указанной главе. Для одиночной игры проверка всегда возвращает `false`.
```json
{ "partnerCurrentlyOnChapter": "chapter_6" }
```
- `partnerPassedChapter`: Проверяет, прошёл ли уже партнёр указанную главу. Для одиночной игры проверка всегда возвращает `false`.
```json
{ "partnerPassedChapter": "chapter_5" }
```
- `healthLess`: Проверяет, меньше ли здоровье, чем указанный уровень.
```json
{ "healthLess": 10 }
```
- `healthEquals`: Проверяет, равно ли здоровье указанному уровню.
```json
{ "healthEquals": 10 }
```
- `healthMore`: Проверяет, больше ли здоровье, чем указанный уровень.
```json
{ "healthMore": 10 }
```
- `or`: Проверяет, соблюдается ли хотя бы одно условие в массиве.
```json
{ "or": [
    {"healthLess": 10}, {"healthEqual": 10}
]}
```
- `and`: Проверяет, соблюдаются ли все условия в массиве.
```json
{ "and": [
    {"hasKnowledge": "i_am_steve"}, {"healthMore": 10}
]}
```
- `not`: Инвертирует условие.
```json
{
    "not": { "hasKnowledge": "this_guy_is_such_a_toolbag" }
}
```

### Эффекты

Эффекты — дополнительные действия, происходящие в данном фрейме. Они позволяют управлять сценой, музыкой, здоровьем персонажей и другими аспектами игры. Эффекты выполняются последовательно, в порядке их объявления.

#### Структура эффекта
Каждый эффект представлен объектом со следующими полями:

- `type`: Тип эффекта.
- `args`: Объект с аргументами, специфичными для данного эффекта.
- `if` (опционально): Условие, при котором эффект выполняется.

#### Типы эффектов и их аргументы
- `JUMP`: Переход к указанному фрейму. После этого эффекта другие эффекты в этом фрейме не выполняются.
  - `frameId`: `id` фрейма, к которому нужно перейти.
- `JUMP_CHAPTER`: Переход к указанной главе (начиная с её первого фрейма). После этого эффекта другие эффекты в этом фрейме не выполняются.
  - `chapterId`: `id` главы, к которой нужно перейти.
- `FADE_IN_SCENE`: Плавное появление сцены.
  - `duration` (опционально): Длительность эффекта в секундах (по умолчанию 1).
- `FADE_OUT_SCENE`: Плавное исчезновение сцены.
  - `duration` (опционально): Длительность эффекта в секундах (по умолчанию 1).
- `FADE_IN_CHARACTER`: Плавное появление персонажа.
  - `characterId`: `id` персонажа.
  - `duration` (опционально): Длительность эффекта в секундах (по умолчанию 1).
- `FADE_OUT_CHARACTER`: Плавное исчезновение персонажа.
  - `characterId`: `id` персонажа.
  - `duration` (опционально): Длительность эффекта в секундах (по умолчанию 1).
- `CHANGE_POSE`: Изменение позы персонажа.
  - `characterId`: `id` персонажа.
  - `newPose`: Новая поза.
- `PLAY_MUSIC`: Воспроизведение музыкального трека.
  - `musicId`: `id` трека.
  - `fadeInDuration` (опционально): Длительность плавного увеличения громкости в секундах.
- `STOP_MUSIC`: Остановка текущего трека.
  - `fadeOutDuration` (опционально): Длительность плавного уменьшения громкости в секундах.
- `INCREASE_HEALTH`: Увеличение здоровья протагониста.
  - `amount`: Количество здоровья для восстановления. Может быть числом или строкой `'full'`.
- `DECREASE_HEALTH`: Уменьшение здоровья протагониста.
  - `amount`: Количество здоровья для уменьшения. Может быть числом или строкой `'kill'`.
- `START_TASK`: Начало задания.
  - `taskId`: `id` задания.
- `OPEN_KNOWLEDGE`: Открытие нового знания в базе знаний.
  - `knowledgeId`: `id` знания.
- `ENDING`: Переход к концовке. После этого эффекта другие эффекты в этом фрейме не выполняются.
  - `endingId`: `id` концовки, к которой нужно перейти.

Пример эффекта с аргументами:
```json
{
  "type": "ENDING",
  "args": {
    "endingId": "badEnding"
  },
  "if": {
    
  }
}
```

## Концовки
Структура концовки:
- `id`: Уникальный идентификатор концовки.
- `title`: Название концовки.
- `condition`: Условие достижения концовки (проверяется после достижения каждого фрейма). Если его не указать, концовка не триггерится автоматически.
- `frames`: Список фреймов, составляющих концовку.
Пример концовки:
```json
{
  "id": "bad_ending",
  "title": "Печальный исход",
  "condition": {
    "variableLess": {
      "variable": "health",
      "value": 1
    }
  },
  "frames": [
    {
      "id": "ending_frame_1",
      "dialogue": "Ваше путешествие окончилось...",
      "speaker": "narrator"
    }
  ]
}
```

## Задания

Секция `tasks` описывает задачи, которые игрок должен выполнить в ходе игры. Задания могут быть разного типа и служат для проверки знаний игрока, продвижения сюжета или предоставления выбора, влияющего на дальнейшее развитие истории.

### Типы заданий

1. **writeKnowledge**:  
   Игрок должен составить факт или правило в формате Prolog. Введенное знание будет проверено на тестовых случаях.

2. **completeQuery**:  
   Игроку предлагается дополнить запрос в формате Prolog. Результат выполнения запроса будет проверен на корректность.

3. **selectOne**:  
   Игрок выбирает один вариант из предложенных. Используется для принятия решений или проверки знаний.

4. **selectMany**:  
   Игрок может выбрать несколько вариантов из списка. Подходит для заданий, где правильным ответом может быть более одного варианта.

### Структура задания

Каждое задание представлено объектом со следующими полями:

- `id`: Уникальный идентификатор задания.
- `type`: Тип задания (`writeKnowledge`, `completeQuery`, `selectOne`, `selectMany`).
- `questionText`: Текст задания или вопроса, предъявляемого игроку.
- `options` (для `selectOne` и `selectMany`): Массив вариантов ответа. Каждый вариант может быть строкой или объектом с дополнительными данными.
- `correctAnswerIndices`: Правильный ответ или ответы. Для:
  - `writeKnowledge` и `completeQuery`: Ожидаемый результат или список тестовых случаев.
  - `selectOne`: Индекс или значение правильного варианта.
  - `selectMany`: Массив индексов или значений правильных вариантов.
- `testCases` (для `writeKnowledge`): Массив объектов, каждый из которых содержит входные данные и ожидаемый результат для проверки введенного игроком знания или запроса.
- `expectedResult` (для `completeQuery`): Массив ожидаемых результатов выполнения запроса, который напишет игрок.
- `hint` (опционально): Подсказка, которую игрок может запросить.
- `nextFrameOnSuccess`: `id` фрейма, к которому переходит игра при успешном выполнении задания.
- `nextFrameOnFailure`: `id` фрейма, к которому переходит игра при неудачном выполнении задания.
- `failureScorePenalty` (опционально): Штраф к очкам или здоровью игрока за неправильный ответ.
- `default` (опционально): Значение по умолчанию или стандартный вариант, если игрок не сделал выбор.

### Примеры заданий

#### Тип `writeKnowledge`

Игрок должен написать факт или правило в формате Prolog.

```json
{
  "id": "task_define_parent",
  "type": "writeKnowledge",
  "questionText": "Определите факт, что Анна является родителем Боба.",
  "testCases": [
    {
      "input": "parent(anna, bob).",
      "expectedResult": true
    }
  ],
  "hint": "Используйте формат parent(Parent, Child).",
  "nextFrameOnSuccess": "frame_success",
  "nextFrameOnFailure": "frame_failure",
  "failureScorePenalty": 10
}
```

#### Тип `completeQuery`

Игрок должен дополнить запрос так, чтобы получить правильный ответ.

```json
{
  "id": "task_complete_query",
  "type": "completeQuery",
  "questionText": "Какой запрос нужно выполнить, чтобы узнать родителей Боба?",
  "expectedResult": ["anna"],
  "default": "parent(% ...",
  "hint": "Запрос должен начинаться с parent(...).",
  "nextFrameOnSuccess": "frame_success_query",
  "nextFrameOnFailure": "frame_failure_query",
  "failureScorePenalty": 5
}
```

#### Тип `selectOne`

Игрок выбирает один правильный вариант.

```json
{
  "id": "task_choose_weapon",
  "type": "selectOne",
  "questionText": "Какое оружие наиболее эффективно против драконов?",
  "options": [
    "Меч",
    "Лук",
    "Копье",
    "Магический жезл"
  ],
  "correctAnswerIndices": 3,  // Индекс правильного варианта (начиная с 0)
  "hint": "Драконы уязвимы к магии.",
  "nextFrameOnSuccess": "frame_weapon_success",
  "nextFrameOnFailure": "frame_weapon_failure",
  "failureScorePenalty": 2
}
```

#### Тип `selectMany`

Игрок выбирает несколько правильных вариантов.

```json
{
  "id": "task_select_supplies",
  "type": "selectMany",
  "questionText": "Выберите предметы, необходимые для похода в горы:",
  "options": [
    "Теплая одежда",
    "Солнцезащитные очки",
    "Купальник",
    "Бутылка воды"
  ],
  "correctAnswerIndices": [0, 1, 3],
  "hint": "Подумайте о погодных условиях в горах.",
  "nextFrameOnSuccess": "frame_supplies_success",
  "nextFrameOnFailure": "frame_supplies_failure",
  "failureScorePenalty": 3
}
```

### Проверка ответов

- Для заданий типа `writeKnowledge` и `completeQuery` ввод игрока проверяется на соответствие ожидаемым результатам с помощью указанных `testCases`.
- Для `selectOne` и `selectMany` выбор игрока сравнивается с `correctAnswer`.

### Переходы между фреймами

- **Успех**: Если игрок успешно выполняет задание, игра переходит к фрейму, указанному в `nextFrameOnSuccess`.
- **Неудача**: В случае неудачи переход происходит к `nextFrameOnFailure`, и может быть применен штраф `failureScorePenalty`.
  - Отнимание здоровья должно быть реализовано отдельно с помощью эффекта фрейма.

## Знания

Секция `knowledge` содержит факты и правила Prolog, которые игрок открывает по мере прохождения игры. Эти знания могут быть использованы для решения задач и принятия решений.

### Структура знания

Каждое знание представлено объектом со следующими полями:

- `id`: Уникальный идентификатор знания.
- `type`: Тип знания (`fact` или `rule`).
- `content`: Текст факта или правила в формате Prolog.
- `description`: Описание или пояснение к знанию (опционально).

### Пример знаний

#### Факт

```json
{
  "id": "knowledge_parent_anna_bob",
  "type": "fact",
  "content": "parent(anna, bob).",
  "description": "Анна является родителем Боба."
}
```

#### Правило

```json
{
  "id": "knowledge_grandparent",
  "type": "rule",
  "content": "grandparent(X, Z) :- parent(X, Y), parent(Y, Z).",
  "description": "Определение бабушки или дедушки через родителей."
}
```

### Открытие знаний

Знания могут быть открыты игроком через эффекты в фреймах или при выполнении заданий.

**Пример эффекта открытия знания:**

```json
{
  "type": "OPEN_KNOWLEDGE",
  "args": {
    "knowledgeId": "knowledge_grandparent"
  }
}
```