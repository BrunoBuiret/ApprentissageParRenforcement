package agent.rlagent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import environnement.Action;
import environnement.Environnement;
import environnement.Etat;
import java.util.Map;
import java.util.Objects;
import javafx.util.Pair;

/**
 * @author laetitiamatignon
 * @author Bruno Buiret (bruno.buiret@etu.univ-lyon1.fr)
 * @author Thomas Arnaud (thomas.arnaud@etu.univ-lyon1.fr)
 */
public class QLearningAgent extends RLAgent
{
    /**
     * A map containing every Q value.
     */
    protected Map<Pair<Etat, Action>, Double> qValues;

    /**
     *
     * @param alpha
     * @param gamma
     * @param _env
     */
    public QLearningAgent(double alpha, double gamma, Environnement _env)
    {
        super(alpha, gamma, _env);

        this.qValues = new HashMap<>();
    }

    /**
     * renvoie la (les) action(s) de plus forte(s) valeur(s) dans l'etat e
     * renvoie liste vide si aucunes actions possibles dans l'etat
     *
     * @param e
     * @return
     */
    @Override
    public List<Action> getPolitique(Etat e)
    {
        List<Action> policy = new ArrayList<>();
        List<Action> availableActions = this.getActionsLegales(e);
        Double maxQ = 0.;
        Double currentQ;

        for(Action action : availableActions)
        {
            currentQ = this.getQValeur(e, action);
            
            if(currentQ > maxQ)
            {
                policy.clear();
                policy.add(action);
                maxQ = currentQ;
            }
            else if(Objects.equals(currentQ, maxQ))
            {
                policy.add(action);
            }
        }

        return policy;
    }

    /**
     * @param e
     * @return la valeur d'un etat
     */
    @Override
    public double getValeur(Etat e)
    {
        List<Action> actions = this.getPolitique(e);

        if(actions.size() > 0)
        {
            Double max = 0.;

            for(Action action : actions)
            {
                if(this.getQValeur(e, action) > max)
                {
                    max = this.getQValeur(e, action);
                }
            }

            return max;
        }

        return 0.;
    }

    /**
     * @param e
     * @param a
     * @return Q valeur du couple (e,a)
     */
    @Override
    public double getQValeur(Etat e, Action a)
    {
        return this.qValues.getOrDefault(new Pair(e, a), 0.);
    }

    /**
     * setter sur Q-valeur
     */
    @Override
    public void setQValeur(Etat e, Action a, double d)
    {
        this.qValues.put(new Pair(e, a), d);

        //mise a jour vmin et vmax pour affichage gradient de couleur
        for(Map.Entry<Pair<Etat, Action>, Double> entry : this.qValues.entrySet())
        {
            if(this.vmax < entry.getValue())
            {
                this.vmax = entry.getValue();
            }

            if(this.vmin > entry.getValue())
            {
                this.vmin = entry.getValue();
            }
        }

        this.notifyObs();
    }

    /**
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
    public void endStep(Etat e, Action a, Etat esuivant, double reward)
    {
        Double value = (1 - this.alpha) * this.getQValeur(e, a) + this.alpha * (reward + this.gamma * this.getValeur(esuivant));
        this.setQValeur(e, a, value);

    }

    /**
     *
     * @param e
     * @return
     */
    @Override
    public Action getAction(Etat e)
    {
        this.actionChoisie = this.stratExplorationCourante.getAction(e);
        return this.actionChoisie;
    }

    /**
     * reinitialise les Q valeurs
     */
    @Override
    public void reset()
    {
        super.reset();
        this.episodeNb = 0;

        this.qValues.clear();

        this.notifyObs();
    }
}
