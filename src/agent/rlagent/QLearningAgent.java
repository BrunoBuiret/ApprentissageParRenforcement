package agent.rlagent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import environnement.Action;
import environnement.Environnement;
import environnement.Etat;
import java.util.Map;
import javafx.util.Pair;

/**
 *
 * @author laetitiamatignon
 *
 */
public class QLearningAgent extends RLAgent {

    /**
     * 
     */
    protected Map<Pair<Etat, Action>, Double> qValues;

    /**
     *
     * @param alpha
     * @param gamma
     * @param _env
     */
    public QLearningAgent(double alpha, double gamma, Environnement _env) {
        super(alpha, gamma, _env);
        
        this.qValues = new HashMap<>();
    }

    /**
     * renvoie la (les) action(s) de plus forte(s) valeur(s) dans l'etat e
     *
     * renvoie liste vide si aucunes actions possibles dans l'etat
     */
    @Override
    public List<Action> getPolitique(Etat e) {
        List<Action> policy = new ArrayList<>();
        List<Action> availableActions = this.getActionsLegales(e);
        Double maxQ = - Double.MAX_VALUE;
        Double currentQ;
        
        for (Action action : availableActions) {
            currentQ = this.getQValeur(e, action);
            if (currentQ > maxQ) {
                policy.clear();
                policy.add(action);
                maxQ = currentQ;
            } else if (currentQ == maxQ) {
                policy.add(action);
            }
        }
        
        return policy;
    }

    /**
     * @return la valeur d'un etat
     */
    @Override
    public double getValeur(Etat e) {
        List<Action> actions = this.getPolitique(e);
        
        if (actions.size() > 0) {
            Double sum = 0.;
            
            for (Action action : actions) {
                sum += this.getQValeur(e, action);
            }
            
            return sum / actions.size();
        }
        
        return - Double.MAX_VALUE;

    }

    /**
     * @param e
     * @param a
     * @return Q valeur du couple (e,a)
     */
    @Override
    public double getQValeur(Etat e, Action a) {
        return this.qValues.getOrDefault(new Pair(e, a), - Double.MAX_VALUE);
    }

    /**
     * setter sur Q-valeur
     */
    @Override
    public void setQValeur(Etat e, Action a, double d) {
        this.qValues.put(new Pair(e, a), d);

        //mise a jour vmin et vmax pour affichage gradient de couleur
        for (Map.Entry<Pair<Etat, Action>, Double> entry: this.qValues.entrySet()) {
            if (this.vmax < entry.getValue()) {
                this.vmax = entry.getValue();
            }
            
            if (this.vmin > entry.getValue()) {
                this.vmin = entry.getValue();
            }
        }
        
        this.notifyObs();
    }

    /**
     *
     * mise a jour de la Q-valeur du couple (e,a) apres chaque interaction
     * <etat e,action a, etatsuivant esuivant, recompense reward>
     * la mise a jour s'effectue lorsque l'agent est notifie par l'environnement
     * apres avoir realise une action.
     *
     * @param e
     * @param a
     * @param esuivant
     * @param reward
     */
    @Override
    public void endStep(Etat e, Action a, Etat esuivant, double reward) {
        Double maxQ = - Double.MAX_VALUE;
        for (Map.Entry<Pair<Etat, Action>, Double> entry : this.qValues.entrySet()) {
            if (entry.getKey().getKey().equals(e) && maxQ < entry.getValue()) {
                maxQ = entry.getValue();
            }
        }
        Double value = (1 - this.alpha) * this.getQValeur(e, a) + alpha * (reward + this.gamma * maxQ);
        this.setQValeur(e, a, value);

    }

    @Override
    public Action getAction(Etat e) {
        this.actionChoisie = this.stratExplorationCourante.getAction(e);
        return this.actionChoisie;
    }

    /**
     * reinitialise les Q valeurs
     */
    @Override
    public void reset() {
        super.reset();
        this.episodeNb = 0;
        
        this.qValues.clear();

        this.notifyObs();
    }

}
