# LocalSearch: Optimización de la Distribución de Bicicletas en Bicing

## Descripción del Proyecto

Este proyecto aborda el problema de optimización de la distribución de bicicletas en el sistema de Bicing. El objetivo principal es mejorar la forma en que las bicicletas se distribuyen en las estaciones para garantizar que los usuarios siempre tengan acceso a ellas cuando las necesiten. Para lograr esto, hemos implementado y evaluado varias estrategias utilizando algoritmos de búsqueda local.

## Estructura del Proyecto

### Implementación del Estado

El estado se representa como una asignación de rutas a furgonetas que pasan por estaciones recogiendo o dejando bicicletas en ellas. Utilizamos las siguientes estructuras de datos:

- **Estaciones**: Información sobre las estaciones de la ciudad.
- **Rutas**: Array de recorridos para cada furgoneta, representados como tripletas de paradas.
- **Paradas**: Información sobre cada parada en una ruta.
- **Vectores Auxiliares**: `startStations` y `impactStations` para optimización rápida.
- **Matriz de Distancias**: Distancias pre-calculadas entre estaciones para eficiencia.

### Justificación de Operadores

Hemos implementado cuatro operadores esenciales para la modificación de las rutas:

1. **addStop(int van, int station)**: Añade una estación al final de la ruta de una furgoneta.
2. **removeStop(int van)**: Elimina la última estación de la ruta de una furgoneta.
3. **switchStop(int van, int pos, int newStation)**: Sustituye una estación en una posición específica de la ruta de una furgoneta.
4. **jumpStartRoute(int van, int origStation, int destStation)**: Inicia una nueva ruta desde una estación de origen a una de destino.

### Estrategia para Hallar la Solución Inicial

Para encontrar una solución inicial, hemos desarrollado una estrategia basada en criterios heurísticos que permiten una aproximación rápida y eficiente al problema.

### Funciones Heurísticas

Implementamos varias funciones heurísticas para evaluar y mejorar las soluciones obtenidas. Estas funciones consideran factores como la distancia, la demanda de bicicletas en cada estación y las capacidades de carga de las furgonetas.

## Experimentos

### Conjunto de Operadores

Realizamos diversos experimentos para determinar el conjunto óptimo de operadores. Evaluamos combinaciones de operadores para encontrar las que ofrecen los mejores resultados en términos de eficiencia y calidad de la solución.

### Estrategia de Solución Inicial

Probamos diferentes estrategias para establecer la solución inicial y comparamos su efectividad. 

### Parámetros para Simulated Annealing (SA)

Ajustamos y evaluamos los parámetros para el algoritmo de Simulated Annealing, optimizando su rendimiento para nuestro problema específico.

### Tiempo de Ejecución en Función del Tamaño

Analizamos cómo varía el tiempo de ejecución del algoritmo en función del tamaño del problema, considerando el número de estaciones y furgonetas involucradas.

### Diferencias entre Heurísticos

Comparamos distintos heurísticos para determinar cuál ofrece los mejores resultados en diferentes escenarios.

### En Hora Punta

Evaluamos la efectividad de las soluciones durante las horas punta, cuando la demanda de bicicletas es máxima.

### Furgonetas Óptimas

Determinamos el número óptimo de furgonetas necesario para cubrir la demanda de bicicletas en la ciudad de manera eficiente.

## Competencia de Trabajo en Equipo: Trabajo de Innovación

### Descripción del Tema

El trabajo en equipo se centró en la innovación de métodos y algoritmos para mejorar la distribución de bicicletas.

### Reparto del Trabajo

Cada miembro del equipo tuvo responsabilidades específicas, desde la implementación de algoritmos hasta la realización de experimentos y análisis de resultados.

### Lista de Referencias

Se incluyeron referencias a trabajos previos y estudios relevantes que respaldaron nuestras decisiones metodológicas.

### Dificultades para Recolectar Información

Identificamos desafíos en la recopilación de información precisa debido a la disponibilidad limitada y la naturaleza evolutiva del campo de investigación.

---

**Nota**: Para más detalles sobre la implementación y los resultados de los experimentos, consulte el documento completo adjunto.

