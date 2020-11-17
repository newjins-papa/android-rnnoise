#ifndef RNN_H_
#define RNN_H_

#include "opus_types.h"

#define WEIGHTS_SCALE (1.f/256)

#define MAX_NEURONS 128

#define ACTIVATION_TANH    0
#define ACTIVATION_SIGMOID 1
#define ACTIVATION_RELU    2

typedef signed char rnn_weight;

typedef struct {
  const rnn_weight *bias;
  const rnn_weight *input_weights;
  int nb_inputs;
  int nb_neurons;
  int activation;
} DenseLayer;

typedef struct {
  const rnn_weight *bias;
  const rnn_weight *input_weights;
  const rnn_weight *recurrent_weights;
  int nb_inputs;
  int nb_neurons;
  int activation;
} GRULayer;

typedef struct RNNState RNNState;

void compute_dense(const DenseLayer *layer, float *output, const float *input);

void compute_gru(const GRULayer *gru, float *state, const float *input);

void compute_rnn(RNNState *rnn, float *gains, float *vad, const float *input);

#endif /* _MLP_H_ */
